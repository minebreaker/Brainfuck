package brainfuck.bytecode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

public final class Compiler {

    public static void main(String[] args) throws IOException {

        checkArgument(args.length == 2, "Argument size must be 2");
        String inputFile = args[0];
        Path inputPath = Paths.get(inputFile);
        checkState(Files.exists(inputPath), "File not found.");

        String outputFile = args[1];
        Path outputDir = Paths.get(outputFile);
        checkState(Files.exists(outputDir), "Output directory does not exist");
        Path outputPath = outputDir.resolve("Main.class");
        checkState(Files.notExists(outputPath), "Output file '%s' already exists.", outputDir.toString());

        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(outputPath));
             InputStream is = new BufferedInputStream(Files.newInputStream(inputPath))) {
            compile(is, os);
        }
    }

    public static void compile(InputStream is, OutputStream os) throws IOException {

        FluentByteWriter w = new FluentByteWriter(os);

        w.write(
                0xCA, 0xFE, 0xBA, 0xBE, // CAFEBABE
                0x00, 0x00,  // miner version: 0
                0x00, 0x31,  // major version: 49  // Version 49 doesn't require stack map

                0x00, 0x20,  // constant pool count: 31 + 1
                // constant pool
                0x07, 0x00, 0x02,  // 1. class: Main
                0x01, 0x00, 0x04,  // 2. utf8
                "Main",
                0x07, 0x00, 0x04,  // 3. class: java/lang/Object
                0x01, 0x00, 0x10,  // 4. utf8
                "java/lang/Object",

                // System.out.print
                0x09, 0x00, 0x06, 0x00, 0x08,  // 5. fieldref System.out
                0x07, 0x00, 0x07,  // 6. class
                0x01, 0x00, 0x10,  // 7. utf8
                "java/lang/System",
                0x0C, 0x00, 0x09, 0x00, 0x0A,  // 8. name and type
                0x01, 0x00, 0x03,  // 9. utf8
                "out",
                0x01, 0x00, 0x15,  // 10. utf8
                "Ljava/io/PrintStream;",
                0x0A, 0x00, 0x0C, 0x00, 0x0E,  // 11. method PrintStream.print(int)
                0x07, 0x00, 0x0D,  // 12. class
                0x01, 0x00, 0x13,  // 13. utf8
                "java/io/PrintStream",
                0x0C, 0x00, 0x0F, 0x00, 0x10,  // 14. name and type
                0x01, 0x00, 0x05,  // 15. utf8
                "print",
                0x01, 0x00, 0x04,  // 16. utf8
                "(C)V",

                // System.in.read(int)
                0x09, 0x00, 0x06, 0x00, 0x12,  // 17. fieldref System.in
                0x0C, 0x00, 0x13, 0x00, 0x14,  // 18. name and type
                0x01, 0x00, 0x02,  // 19. utf8
                "in",
                0x01, 0x00, 0x15,  // 20. utf8
                "Ljava/io/InputStream;",
                0x0A, 0x00, 0x16, 0x00, 0x18,  // 21. method InputStream.read(int)
                0x07, 0x00, 0x17,  // 22. class
                0x01, 0x00, 0x13,  // 23. utf8
                "java/io/InputStream",
                0x0C, 0x00, 0x19, 0x00, 0x1A,  // 24. name and type
                0x01, 0x00, 0x04,  // 25. utf8
                "read",
                0x01, 0x00, 0x3,  // 26. utf8
                "()I",

                // main
                0x01, 0x00, 0x04,  // 27. utf8
                "main",
                0x01, 0x00, 0x16,  // 28. utf8
                "([Ljava/lang/String;)V",
                0x01, 0x00, 0x04,  // 29. utf8
                "args",
                0x01, 0x00, 0x13,  // 30. utf8
                "[Ljava/lang/String;",

                // "Code" for Attribute
                0x01, 0x00, 0x04,  // 31. utf8
                "Code",

                0x00, 0x21,  // access_flags: ACC_SUPER ACC_PUBLIC
                0x00, 0x01,  // this class
                0x00, 0x03,  // super class

                0x00, 0x00,  // interfaces count
                // interfaces[]
                //NOP
                0x00, 0x00,  // fields count
                // fields[]
                // NOP

                0x00, 0x01,  // method count
                // methods[]

                // main
                0x00, 0x09,  // access flags: ACC_PUBLIC ACC_STATIC
                0x00, 0x1B,  // name index: main
                0x00, 0x1C,  // descriptor index
                0x00, 0x01,  // attributes count
                // attribute info
                0x00, 0x1F  // attribute name index: Code
        );
        byte[] code = compileCode(is);
        w.write(
                ByteUtils.toByteArray4(code.length + 12),  // attribute length
                // info
                0x00, 0x04,  // max stack
                0x00, 0x02,  // max locals

                // code length
                ByteUtils.toByteArray4(code.length),
                // code
                code,

                0x00, 0x00,  // exception table length
                // exception table
                // NOP
                0x00, 0x00,  // attribute count
                // attribute info[]
                // NOP

                // class attributes count
                0x00, 0x00
                // attributes
                // NOP
        );
    }

    private static byte[] compileCode(InputStream is) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FluentByteWriter w = new FluentByteWriter(baos);

        // initialize
        w.write(
                // creates data buffer
                0x11, 0x75, 0x30,  // sipush 30000
                0xBC, 0x0A,  // newarray int
                0x4B,  // astore_0  // ignore application arguments (String[] args)
                // creates instruction pointer
                0x03,  // iconst_0
                0x3C   // istore_1
        );
        w.write(
                compileCodeElements(is),
                // return
                0xB1
        );

        return baos.toByteArray();
    }

    private static byte[] compileCodeElements(InputStream is) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FluentByteWriter w = new FluentByteWriter(baos);

        int i;
        while ((i = is.read()) >= 0) {
            switch (i) {
            case ('+'):
                w.write(
                        0x2A,  // aload_0
                        0x1B,  // iload_1
                        0x5C,  // dup2
                        0x2E,  // iaload
                        0x04,  // iconst_1
                        0x60,  // iadd
                        0x4F  // iastore
                );
                break;
            case ('-'):
                w.write(
                        0x2A,  // aload_0
                        0x1B,  // iload_1
                        0x5C,  // dup2
                        0x2E,  // iaload
                        0x02,  // iconst_m1
                        0x60,  // iadd
                        0x4F  // iastore
                );
                break;
            case ('>'):
                w.write(0x84, 0x01, 0x01);  // iinc 1 1
                break;
            case ('<'):
                w.write(0x84, 0x01, 0xFF);  // iinc 1 -1
                break;
            case ('.'):
                w.write(
                        0xB2, 0x00, 0x05,  // getstatic System.out
                        0x2A,  // aload_0
                        0x1B,  // iload_1
                        0x2E,  // iaload
                        0x92,  // i2c
                        0xB6, 0x00, 0x0B  // invokevirtual print(Ljava/lang/String;)V
                );
                break;
            case (','):
                w.write(
                        0x2A,  // aload_0
                        0x1B,  // iload_1
                        0xB2, 0x00, 0x11,  // getstatic System.in
                        0xB6, 0x00, 0x15,  // invokevirtual read()I
                        0x4F  // iastore
                );
                break;
            case ('['):
                int size = baos.size();
                w.write((Object) compileLoop(is));
                break;
            case (']'):
                return baos.toByteArray();
            default:
                // NOP
            }
        }

        return baos.toByteArray();
    }

    private static byte[] compileLoop(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FluentByteWriter w = new FluentByteWriter(baos);

        w.write(
                // load current pointer value
                0x2A,  // aload_0
                0x1B,  // iload_1
                0x2E  // iaload
        );
        byte[] insideLoop = compileCodeElements(is);
        w.write(
                0x99, ByteUtils.toByteArray2((short) (insideLoop.length + 6)), // ifeq  // 4 = ifeq(3) + loop length + goto(3)
                insideLoop,
                0xA7, ByteUtils.toByteArray2((short) -(insideLoop.length + 6))  // goto
        );

        return baos.toByteArray();
    }

}
