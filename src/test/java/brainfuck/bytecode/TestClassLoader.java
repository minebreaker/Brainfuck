package brainfuck.bytecode;

/**
 * Classloader for testing. Always returns the given byte array as a class.
 */
public final class TestClassLoader extends ClassLoader {

    private final byte[] clazz;

    public TestClassLoader(byte[] clazz) {
        this.clazz = clazz;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return defineClass("Main", clazz, 0, clazz.length);
    }

}
