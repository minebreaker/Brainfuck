package brainfuck.bytecode;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompilerTest {

    @Before
    public void setUp() {

    }

    private String compile(String code) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Compiler.compile(bais, baos);
        byte[] clazz = baos.toByteArray();

        TestClassLoader loader = new TestClassLoader(clazz);
        Class<MockMain> target = (Class<MockMain>) loader.loadClass("Main");

        ByteArrayOutputStream redirect = new ByteArrayOutputStream();
        System.setOut(new PrintStream(redirect));
        target.getDeclaredMethod("main", String[].class).invoke(null, (Object) null);

        return new String(redirect.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void test1() throws Exception {
        String ret = compile("++++++++++++++++++++++++++++++++++-.");
        assertThat(ret, is("!"));
    }

    @Test
    public void test2() throws Exception {
        String ret = compile("+++++++++++++++++++++++++++++++++>++++++++++++++++++++++++++++++++++<.>.");
        assertThat(ret, is("!\""));
    }

    @Test
    public void test3() throws Exception {
        String ret = compile("++++++++[>++++++++<-]>+.");
        assertThat(ret, is("A"));
    }

}