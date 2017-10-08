package brainfuck.bytecode;

import com.google.common.io.CharStreams;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompilerTest {

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

    /**
     * Test "+-.".
     */
    @Test
    public void test1() throws Exception {
        String ret = compile("++++++++++++++++++++++++++++++++++-.");
        assertThat(ret, is("!"));
    }

    /**
     * Test "&lt;&gt;,".
     */
    @Test
    public void test2() throws Exception {
        System.setIn(new ByteArrayInputStream("A".getBytes()));
        String ret = compile(",>+++++++++++++++++++++++++++++++++<.>.");
        assertThat(ret, is("A!"));
    }

    /**
     * Test "[]".
     */
    @Test
    public void test3() throws Exception {
        String ret = compile("++++++++[>++++++++<-]>+.");
        assertThat(ret, is("A"));
    }

    /**
     * Test nested loop.
     */
    @Test
    public void test4() throws Exception {
        String ret = compile("++++[>++++[>++++<-]<-]>>+.");
        assertThat(ret, is("A"));
    }

    @Test
    public void testMain() throws Exception {
        Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

        Path in = Paths.get(this.getClass().getResource("/helloworld.bf").toURI());
        Path out = Files.createTempDirectory(null);
        Compiler.main(new String[] { in.toAbsolutePath().toString(), out.toAbsolutePath().toString() });

        Process p = new ProcessBuilder("java", "Main")
                .directory(out.toFile())
                .start();
        int resultCode = p.waitFor();
        logger.info("Status: " + resultCode);
        String resultOut = CharStreams.toString(new InputStreamReader(p.getInputStream()));
        logger.info("Stdin:  " + resultOut);
        String stderr = CharStreams.toString(new InputStreamReader(p.getErrorStream(), Charset.forName("SJIS")));  // Change charset for your own
        logger.info("Stderr: " + stderr);

        assertThat(resultCode, is(0));
        assertThat(resultOut, is("Hello World!\n"));
    }

}
