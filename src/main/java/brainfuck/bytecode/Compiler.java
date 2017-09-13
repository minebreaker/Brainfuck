package brainfuck.bytecode;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

public final class Compiler {

    public static void main(String[] args) throws IOException {

//        checkArgument(args.length == 1, "Argument size must be 2");
//        String inputFile = args[0];

        // TODO
//        Path inputPath = Paths.get(inputFile);
//        checkState(Files.exists(inputPath), "File not found.");


        Path outputPath = Paths.get("Main.class");
//        checkState(Files.notExists(outputPath), "Output file already exists.");

        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(outputPath))) {
            compile(null, os);
        }
    }


    public static void compile(InputStream is, OutputStream os) throws IOException {

        os.write(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE }); // CAFEBABE
        os.write(new byte[] { 0x00, 0x00 });  // miner version: 0
        os.write(new byte[] { 0x00, 0x34 });  // major version: 52

        os.write(new byte[] { 0x00, 0x18 });  // constant pool count: 23 + 1
        // constant pool
        os.write(new byte[] { 0x07, 0x00, 0x02 });  // 1. class: Main
        os.write(new byte[] { 0x01, 0x00, 0x04 });  // 2. utf8
        os.write("Main".getBytes());
        os.write(new byte[] { 0x07, 0x00, 0x04 });  // 3. class: java/lang/Object
        os.write(new byte[] { 0x01, 0x00, 0x10 });  // 4. utf8
        os.write("java/lang/Object".getBytes());

        // System.out.print
        os.write(new byte[] { 0x09, 0x00, 0x06, 0x00, 0x08 });  // 5. fieldref System.out
        os.write(new byte[] { 0x07, 0x00, 0x07 });  // 6. class
        os.write(new byte[] { 0x01, 0x00, 0x10 });  // 7. utf8
        os.write("java/lang/System".getBytes());
        os.write(new byte[] { 0x0C, 0x00, 0x09, 0x00, 0x0A });  // 8. name and type
        os.write(new byte[] { 0x01, 0x00, 0x03 });  // 9. utf8
        os.write("out".getBytes());
        os.write(new byte[] { 0x01, 0x00, 0x15 });  // 10. utf8
        os.write("Ljava/io/PrintStream;".getBytes());

        os.write(new byte[] { 0x0A, 0x00, 0x0C, 0x00, 0xE });  // 11. method PrintStream.print(String)
        os.write(new byte[] { 0x07, 0x00, 0x0D });  // 12. class
        os.write(new byte[] { 0x01, 0x00, 0x13 });  // 13. utf8
        os.write("java/io/PrintStream".getBytes());
        os.write(new byte[] { 0x0C, 0x00, 0x0F, 0x00, 0x10 });  // 14. name and type
        os.write(new byte[] { 0x01, 0x00, 0x05 });  // 15. utf8
        os.write("print".getBytes());
        os.write(new byte[] { 0x01, 0x00, 0x15 });  // 16. utf8
        os.write("(Ljava/lang/String;)V".getBytes());

        // String
        os.write(new byte[] { 0x08, 0x00, 0x12 });  // 17. string info
        os.write(new byte[] { 0x01, 0x00, 0x0B });  // 18. utf8
        os.write("Hello, JVM!".getBytes());

        // main
        os.write(new byte[] { 0x01, 0x00, 0x04 });  // 19. utf8
        os.write("main".getBytes());
        os.write(new byte[] { 0x01, 0x00, 0x16 });  // 20. utf8
        os.write("([Ljava/lang/String;)V".getBytes());
        os.write(new byte[] { 0x01, 0x00, 0x04 });  // 21. utf8
        os.write("args".getBytes());
        os.write(new byte[] { 0x01, 0x00, 0x13 });  // 22. utf8
        os.write("[Ljava/lang/String;".getBytes());

        // "Code" for Attribute
        os.write(new byte[] { 0x01, 0x00, 0x04 });  // 23. utf8
        os.write("Code".getBytes());

        os.write(new byte[] { 0x00, 0x21 });  // access_flags: ACC_SUPER ACC_PUBLIC
        os.write(new byte[] { 0x00, 0x01 });  // this class
        os.write(new byte[] { 0x00, 0x03 });  // super class

        os.write(new byte[] { 0x00, 0x00 });  // interfaces count
        // interfaces[]
        //NOP
        os.write(new byte[] { 0x00, 0x00 });  // fields count
        // fields[]
        // NOP

        os.write(new byte[] { 0x00, 0x01 });  // method count
        // methods[]

        // main
        os.write(new byte[] { 0x00, 0x09 });  // access flags: ACC_PUBLIC ACC_STATIC
        os.write(new byte[] { 0x00, 0x13 });  // name index: main
        os.write(new byte[] { 0x00, 0x14 });  // descriptor index
        os.write(new byte[] { 0x00, 0x01 });  // attributes count
        // attribute info
        os.write(new byte[] { 0x00, 0x17 });  // attribute name index: Code
        os.write(new byte[] { 0x00, 0x00, 0x00, 0x15 });  // attribute length
        // info
        os.write(new byte[] { 0x00, 0x02 });  // max stack
        os.write(new byte[] { 0x00, 0x01 });  // max locals
        // code length
        os.write(new byte[] { 0x00, 0x00, 0x00, 0x09 });
        // code
        os.write(new byte[] { (byte) 0xB2, 0x00, 0x05 });  // getstatic Field java/lang/System.out:Ljava/io/PrintStream;
        os.write(new byte[] { 0x12, 0x11 });  // ldc String hello, JVM!
        os.write(new byte[] { (byte) 0xB6, 0x00, 0x0B });  // invokevirtual Method java/io/PrintStream.println:(Ljava/lang/String;)V
        os.write(new byte[] { (byte) 0xB1 });  // return

        os.write(new byte[] { 0x00, 0x00 });  // exception table length
        // exception table
        // NOP
        os.write(new byte[] { 0x00, 0x00 });  // attribute count
        // attribute info[]
        // NOP

        // class attributes count
        os.write(new byte[] { 0x00, 0x00 });
        // attributes
        // NOP
    }

}
