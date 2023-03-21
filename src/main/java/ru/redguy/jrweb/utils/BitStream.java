package ru.redguy.jrweb.utils;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BitStream {
    int byteCarriage = 0;
    byte currentByte = -1;
    LinkedBlockingQueue<Byte> bytes = new LinkedBlockingQueue<>();

    public BitStream() {
    }

    public void addByte(byte b) {
        bytes.add(b);
    }

    public boolean getBit() {
        if(currentByte == -1) {
            try {
                currentByte = bytes.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        boolean result = (currentByte & (1 << byteCarriage)) != 0;
        byteCarriage++;
        if(byteCarriage == 8) {
            byteCarriage = 0;
            currentByte = -1;
        }
        return result;
    }

    public boolean hasBits() {
        return !bytes.isEmpty() || byteCarriage != 0;
    }
}
