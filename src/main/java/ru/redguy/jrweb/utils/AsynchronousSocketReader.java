package ru.redguy.jrweb.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsynchronousSocketReader {
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer readBuffer;

    public AsynchronousSocketReader(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.readBuffer = ByteBuffer.allocate(1); // Adjust the buffer size as needed
    }

    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        while (true) {
            try {
                if (socketChannel.read(readBuffer).get() == -1) break;
            } catch (InterruptedException | ExecutionException e) {
            }
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                char c = (char) readBuffer.get();
                if (c == '\n') {
                    //if starts with \r then remove it
                    if (line.length() > 0 && line.charAt(line.length() - 1) == '\r') {
                        line.deleteCharAt(line.length() - 1);
                    }
                    readBuffer.clear();
                    return line.toString();
                }
                line.append(c);
            }
            readBuffer.clear();
        }
        return ""; // No complete line found
    }

    public String readString(int length) {
        StringBuilder line = new StringBuilder();
        while (line.length() < length) {
            try {
                if (socketChannel.read(readBuffer).get() == -1) break;
            } catch (InterruptedException | ExecutionException e) {
            }
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                char c = (char) readBuffer.get();
                line.append(c);
            }
            readBuffer.clear();
        }
        return line.toString();
    }

    public ByteBuffer readAllBytes() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        ByteBuffer result = ByteBuffer.allocate(1024);
        while (true) {
            try {
                if (socketChannel.read(byteBuffer).get() == -1) break;
            } catch (InterruptedException | ExecutionException e) {
            }
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                result.put(byteBuffer.get());
            }
            byteBuffer.clear();
        }
        result.flip();
        return result;
    }

    public CompletableFuture<String> asyncReadLine() {
        CompletableFuture<String> future = new CompletableFuture<>();
        readLineAsync(future, new StringBuilder());
        return future;
    }

    private void readLineAsync(CompletableFuture<String> future, StringBuilder line) {
        readBuffer.clear();
        socketChannel.read(readBuffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void attachment) {
                if (bytesRead == -1) {
                    future.complete(line.toString());
                    return;
                }

                readBuffer.flip();
                while (readBuffer.hasRemaining()) {
                    char c = (char) readBuffer.get();
                    if (c == '\n') {
                        future.complete(line.toString());
                        return;
                    }
                    line.append(c);
                }
                readBuffer.clear();
                readLineAsync(future, line);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                future.completeExceptionally(exc);
            }
        });
    }

    public byte readByte() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        try {
            socketChannel.read(byteBuffer).get();
        } catch (InterruptedException | ExecutionException e) {
        }
        byteBuffer.flip();
        return byteBuffer.get();
    }

    public ByteBuffer readBytes(int length) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        try {
            socketChannel.read(byteBuffer).get();
        } catch (InterruptedException | ExecutionException e) {
        }
        byteBuffer.flip();

        // Assuming ByteBuf is your custom class
        return byteBuffer;
    }

    public CompletableFuture<ByteBuffer> asyncReadBytes(int length) {
        CompletableFuture<ByteBuffer> future = new CompletableFuture<>();
        readBytesAsync(length, future);
        return future;
    }

    private void readBytesAsync(int length, CompletableFuture<ByteBuffer> future) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        socketChannel.read(byteBuffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void attachment) {
                if (bytesRead == -1) {
                    future.completeExceptionally(new IOException("End of stream reached."));
                    return;
                }

                if (byteBuffer.remaining() > 0) {
                    // Continue reading until desired length is reached
                    readBytesAsync(byteBuffer.remaining(), future);
                    return;
                }

                byteBuffer.flip();
                future.complete(byteBuffer);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                future.completeExceptionally(exc);
            }
        });
    }
}
