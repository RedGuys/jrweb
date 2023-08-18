package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataOutputStream {
    private final AsynchronousSocketChannel stream;
    private final MultiThreadLock writeLock;
    private final Queue<CompletableFuture<Void>> writeQueue;

    public DataOutputStream(AsynchronousSocketChannel stream) {
        this.stream = stream;
        this.writeLock = new MultiThreadLock();
        this.writeQueue = new ConcurrentLinkedQueue<>();
    }

    public void waitLock() {
        writeLock.waitLock();
    }

    public void write(@NotNull String value) throws Exception {
        writeData(value.getBytes(StandardCharsets.UTF_8)).join();
    }

    public void write(@NotNull String value, Charset charset) throws Exception {
        writeData(value.getBytes(charset)).join();
    }

    public void write(byte[] bytes) throws Exception {
        writeData(bytes).join();
    }

    private @NotNull CompletableFuture<Void> writeData(byte[] data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        writeQueue.offer(future);

        writeLock.lock();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        stream.write(buffer, null, new WriteCompletionHandler(data, future));

        return future;
    }

    private void processNextWrite() {
        CompletableFuture<Void> future = writeQueue.poll();
        if (future != null) {
            future.complete(null);
        }
    }

    private class WriteCompletionHandler implements java.nio.channels.CompletionHandler<Integer, Void> {
        private final byte[] data;
        private final CompletableFuture<Void> future;

        public WriteCompletionHandler(byte[] data, CompletableFuture<Void> future) {
            this.data = data;
            this.future = future;
        }

        @Override
        public void completed(Integer result, Void attachment) {
            if (result < data.length) {
                ByteBuffer buffer = ByteBuffer.wrap(data, result, data.length - result);
                stream.write(buffer, null, this);
            } else {
                writeLock.unlock();
                processNextWrite();
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            // Handle write failure
            writeLock.unlock();
            processNextWrite();
        }
    }
}
