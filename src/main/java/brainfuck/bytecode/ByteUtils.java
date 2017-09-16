package brainfuck.bytecode;

import java.nio.ByteBuffer;

public final class ByteUtils {

    private ByteUtils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] toByteArray(int n) {
        return ByteBuffer.allocate(4).putInt(n).array();
    }

    public static byte[] toByteArray(int n, int size) {
        return ByteBuffer.allocate(size).putInt(n).array();
    }

}
