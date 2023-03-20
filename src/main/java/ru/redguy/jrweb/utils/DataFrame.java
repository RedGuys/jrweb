package ru.redguy.jrweb.utils;

public class DataFrame {

    /*TODO: DataFrame factory
    * bytes -> DataFrameFactory -> DataFrame#isReady() -> BitStream#addByte(byte) -> DataFrame#process() -> DataFrame#isReady() -> DataFrame#getData()
    *                                                  -> DataFrame#getData() -> DataFrame::new -> DataFrame#process() -> DataFrame#isReady() -> DataFrame#getData()
    * */


    enum ReadState {
        FIN,
        RSV,
        OPCODE,
        MASK,
        PAYLOAD_LENGTH,
        PAYLOAD_LENGTH_16,
        PAYLOAD_LENGTH_64,
        MASKING_KEY,
        PAYLOAD_DATA,
        FINISHED
    }

    final BitStream stream;
    ReadState status = ReadState.FIN;
    Bits FIN = new Bits(1);
    Bits RSV = new Bits(3);
    Bits OPCODE = new Bits(4);
    Bits MASK = new Bits(1);
    Bits PAYLOAD_LENGTH = new Bits(7);
    Bits PAYLOAD_LENGTH_16 = new Bits(16);
    Bits PAYLOAD_LENGTH_64 = new Bits(64);
    Bits MASKING_KEY = new Bits(32);
    Bits PAYLOAD_DATA = new Bits(0);

    public DataFrame(BitStream stream) {
        this.stream = stream;
    }

    public void process() {
        while(stream.hasBits()) {
            switch (status) {
                case FIN: {
                    FIN.setBit(0, stream.getBit());
                    status = ReadState.RSV;
                    break;
                }
                case RSV: {
                    RSV.setBit(0, stream.getBit());
                    RSV.setBit(1, stream.getBit());
                    RSV.setBit(2, stream.getBit());
                    status = ReadState.OPCODE;
                    break;
                }
                case OPCODE: {
                    OPCODE.setBit(0, stream.getBit());
                    OPCODE.setBit(1, stream.getBit());
                    OPCODE.setBit(2, stream.getBit());
                    OPCODE.setBit(3, stream.getBit());
                    status = ReadState.MASK;
                    break;
                }
                case MASK: {
                    MASK.setBit(0, stream.getBit());
                    status = ReadState.PAYLOAD_LENGTH;
                    break;
                }
                case PAYLOAD_LENGTH: {
                    for (int i = 0; i < 8; i++) {
                        PAYLOAD_LENGTH.setBit(i, stream.getBit());
                    }
                    if(PAYLOAD_LENGTH.toInt() < 126) {
                        PAYLOAD_DATA = new Bits(PAYLOAD_LENGTH.toInt());
                        if(MASK.getBit(0)) {
                            status = ReadState.MASKING_KEY;
                        } else {
                            status = ReadState.PAYLOAD_DATA;
                        }
                    } else if(PAYLOAD_LENGTH.toInt() == 126) {
                        status = ReadState.PAYLOAD_LENGTH_16;
                    } else if(PAYLOAD_LENGTH.toInt() == 127) {
                        status = ReadState.PAYLOAD_LENGTH_64;
                    }
                    break;
                }
                case PAYLOAD_LENGTH_16: {
                    for (int i = 0; i < 16; i++) {
                        PAYLOAD_LENGTH_16.setBit(i, stream.getBit());
                    }
                    PAYLOAD_DATA = new Bits(PAYLOAD_LENGTH_16.toInt());
                    if(MASK.getBit(0)) {
                        status = ReadState.MASKING_KEY;
                    } else {
                        status = ReadState.PAYLOAD_DATA;
                    }
                }
                case PAYLOAD_LENGTH_64: {
                    for (int i = 0; i < 64; i++) {
                        PAYLOAD_LENGTH_64.setBit(i, stream.getBit());
                    }
                    PAYLOAD_DATA = new Bits(PAYLOAD_LENGTH_64.toInt());
                    if(MASK.getBit(0)) {
                        status = ReadState.MASKING_KEY;
                    } else {
                        status = ReadState.PAYLOAD_DATA;
                    }
                }
                case MASKING_KEY: {
                    for (int i = 0; i < 32; i++) {
                        MASKING_KEY.setBit(i, stream.getBit());
                    }
                    status = ReadState.PAYLOAD_DATA;
                    break;
                }
                case PAYLOAD_DATA: {
                    for (int i = 0; i < PAYLOAD_DATA.getLength(); i++) {
                        PAYLOAD_DATA.setBit(i, stream.getBit());
                    }
                    status = ReadState.FINISHED;
                    break;
                }
            }
        }
    }

    public boolean isReady() {
        return status == ReadState.FINISHED;
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
