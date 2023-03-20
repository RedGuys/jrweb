package ru.redguy.jrweb.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BitStream {
    int byteCarriage = 0;
    byte currentByte = -1;
    Queue<Byte> bytes = new LinkedList<>();

    public BitStream() {
    }

    public void addByte(byte b) {
        bytes.add(b);
    }

    public boolean getBit() {
        if(currentByte == -1) {
            currentByte = bytes.poll();
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
