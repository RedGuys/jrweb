package ru.redguy.jrweb.utils;

public class Bits {
    final int length;
    final boolean[] bits;

    public Bits(int length) {
        this.length = length;
        bits = new boolean[length];
    }

    public void setBit(int index, boolean value) {
        bits[index] = value;
    }

    public boolean getBit(int index) {
        return bits[index];
    }

    public int toInt() {
        int result = 0;
        for (int i = 0; i < length; i++) {
            if (bits[i]) {
                result += Math.pow(2, i);
            }
        }
        return result;
    }

    public byte toByte() {
        return (byte) toInt();
    }

    public short toShort() {
        return (short) toInt();
    }

    public long toLong() {
        long result = 0;
        for (int i = 0; i < length; i++) {
            if (bits[i]) {
                result += Math.pow(2, i);
            }
        }
        return result;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[length / 8];
        for (int i = 0; i < length; i++) {
            if (bits[i]) {
                result[i / 8] += Math.pow(2, i % 8);
            }
        }
        return result;
    }

    public int getLength() {
        return length;
    }
}
