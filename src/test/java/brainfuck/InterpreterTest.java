package brainfuck;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterpreterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ByteArrayOutputStream baos;
    private Interpreter i;

    @Before
    public void setUp() {
        baos = new ByteArrayOutputStream();
        i = new Interpreter(baos, new ByteArrayInputStream(new byte[0]));
    }

    private String result() {
        return new String(baos.toByteArray(), StandardCharsets.UTF_8).intern();
    }

    @Test
    public void helloworld() throws IOException {
        i.interpret(">+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.[-]>++++++++[<++++>-]<.>+++++++++++[<+++++>-]<.>++++++++[<+++>-]<.+++.------.--------.[-]>++++++++[<++++>-]<+.[-]++++++++++.");
        assertThat(result(), is("Hello World!\n"));
    }

    @Test
    public void testNestedLoop() throws IOException {
        i.interpret("++++++++[>++++++++<->>++[>++++++<-]<<]>+.>>+.");
        assertThat(result(), is("Aa"));
    }

    @Test
    public void testInput() throws IOException {
        i = new Interpreter(baos, new ByteArrayInputStream("ABC".getBytes(StandardCharsets.UTF_8)));
        i.interpret(",>,>,<<.>.>.");
        assertThat(result(), is("ABC"));
    }

    @Test
    public void testInit() throws IOException {
        i.interpret("+++++>+++++");
        i.init();
        i.interpret("++++++++[>++++++++<-]>+.");
        assertThat(result(), is("A"));
    }

    @Test
    public void testDataPointerOverflow() throws IOException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Data pointer overflow : 0");

        //noinspection InfiniteLoopStatement
        while (true)
            i.interpret(">");
    }

    @Test
    public void testDataPointerUnderflow() throws IOException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Data pointer underflow : 0");

        i.interpret("<");
    }

    @Test
    public void testNumberOverflow() throws IOException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Number overflow : 2");

        i.interpret("+[+]");
    }

    @Test
    public void testNumberUnderflow() throws IOException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Number underflow : 2");

        i.interpret("-[-]");
    }

}
