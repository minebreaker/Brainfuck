package brainfuck.bytecode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Helper class to create byte array fluently.
 */
public final class FluentByteWriter {

    private final OutputStream os;

    public FluentByteWriter(OutputStream os) {
        this.os = os;
    }

    public FluentByteWriter write(Object... bytes) {

        try {
            for (Object o : bytes) {
                if (o instanceof Integer) {
                    os.write((Integer) o);  // Note high-order bits are ignored
                } else if (o instanceof Byte) {
                    os.write((Byte) o);
                } else if (o instanceof byte[]) {
                    os.write((byte[]) o);
                } else if (o instanceof String) {
                    os.write(((String) o).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new UnsupportedOperationException("Unwritable class: " + o.getClass().getCanonicalName());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

}
