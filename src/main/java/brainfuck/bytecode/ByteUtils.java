package brainfuck.bytecode;

import java.nio.ByteBuffer;

public final class ByteUtils {

    private ByteUtils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] toByteArray4(int n) {
        return ByteBuffer.allocate(4).putInt(n).array();
    }

    public static byte[] toByteArray2(int n) {
        return toByteArray2((short) n);
    }

    public static byte[] toByteArray2(short n) {
        return ByteBuffer.allocate(2).putShort(n).array();
    }

}
