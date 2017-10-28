# Brainfuck

## Brainfuck?
[Wikipedia](https://ja.wikipedia.org/wiki/Brainfuck)

## Interpreter

```java
Interpreter i = new Interpreter(System.out, System.in);
```

Reads InputStream til hit linebreaks and execute the code.

See [`brainfuck.bytecode.InterpreterTest.java`](https://bitbucket.org/minebreaker_tf/brainfuck/src/c068ba129ccfc81fbe76e876f986f714d0f36f22/src/test/java/brainfuck/InterpreterTest.java?at=master&fileviewer=file-view-default).


## Compiler

[解説](https://deadcode.rip/programming/brainfuck/brainfuck)

Compiles given Brainfuck code into Java byte code.

### init

```
// create array of length 100
bipush 100
newarray int
astore_0

// create code pointer
iconst_0
astore_1
```

### compile

```
// +
// read current code point
aload_0
aload_1
dup2
iaload  
// add 1
iconst_1 
iadd
iastore

// -
// read current code point
aload_0
aload_1
dup2
iaload  
// add -1
iconst_-1 
iadd
iastore

// >
iinc 1 1  // increment code pointer by 1

// <
iinc 1 -1  // increment code pointer by -1

// .
getstatic java/lang/System.out:Ljava.io.PrintStream
aload_0
aload_1
iaload
invokevirtual java/io/PrintStream.print:(I)V

// ,
// push array
aload_0
// read from stdin
getstatic java/lang/System.in:Ljava.io.InputStream
invokevirtual java/io/InputStream.read:(I)V

// [
// save current position
// read current number in array
aload_0
iload_1
iaload
// if [i] == 0
ifeq `end of loop`
...  // recursively compile
goto `saved position`
// end of loop

// ]
// NOP
```

### tear down
```
return
```
