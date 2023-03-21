package ru.redguy.jrweb.utils;

public class DataFrame {

    final BitStream stream;
    Bits FIN = new Bits(1);
    Bits RSV = new Bits(3);
    Bits OPCODE = new Bits(4);
    Bits MASK = new Bits(1);
    Bits PAYLOAD_LENGTH = new Bits(7);
    Bits PAYLOAD_LENGTH_16 = new Bits(16);
    Bits PAYLOAD_LENGTH_64 = new Bits(64);
    Bits MASKING_KEY = new Bits(32);
    Bits PAYLOAD_DATA = new Bits(0);
    boolean ready = false;

    public DataFrame(BitStream stream) {
        this.stream = stream;
    }

    public void process() {
        FIN.setBit(0, stream.getBit());
        RSV.setBit(0, stream.getBit());
        RSV.setBit(1, stream.getBit());
        RSV.setBit(2, stream.getBit());
        OPCODE.setBit(0, stream.getBit());
        OPCODE.setBit(1, stream.getBit());
        OPCODE.setBit(2, stream.getBit());
        OPCODE.setBit(3, stream.getBit());
        MASK.setBit(0, stream.getBit());
        for (int i = 0; i < 7; i++) {
            PAYLOAD_LENGTH.setBit(i, stream.getBit());
        }
        if (PAYLOAD_LENGTH.toInt() < 126) {
            PAYLOAD_DATA = new Bits(PAYLOAD_LENGTH.toInt());
        } else if (PAYLOAD_LENGTH.toInt() == 126) {
            for (int i = 0; i < 16; i++) {
                PAYLOAD_LENGTH_16.setBit(i, stream.getBit());
            }
            PAYLOAD_DATA = new Bits(PAYLOAD_LENGTH_16.toInt());
        } else if (PAYLOAD_LENGTH.toInt() == 127) {
            for (int i = 0; i < 64; i++) {
                PAYLOAD_LENGTH_64.setBit(i, stream.getBit());
            }
            PAYLOAD_DATA = new Bits(PAYLOAD_LENGTH_64.toInt());
        }
        if (MASK.getBit(0)) {
            for (int i = 0; i < 32; i++) {
                MASKING_KEY.setBit(i, stream.getBit());
            }
        }
        for (int i = 0; i < PAYLOAD_DATA.getLength(); i++) {
            PAYLOAD_DATA.setBit(i, stream.getBit());
        }
        ready = true;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isFin() {
        return FIN.getBit(0);
    }

    public byte getRSV() {
        return RSV.toByte();
    }

    public byte getOPCODE() {
        return OPCODE.toByte();
    }

    public boolean hasMask() {
        return MASK.getBit(0);
    }

    public long getContentLength() {
        return PAYLOAD_DATA.getLength();
    }

    public byte[] getContent() {
        return PAYLOAD_DATA.toByteArray();
    }

    public byte[] getMaskingKey() {
        return MASKING_KEY.toByteArray();
    }
}
