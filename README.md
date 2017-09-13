# Brainfuck

## Brainfuck?
[Wikipedia](https://ja.wikipedia.org/wiki/Brainfuck)

## Interpreter

```java
Interpreter i = new Interpreter(System.out, System.in);
```

InputStreamを改行まで読み込んで、実行します

## Compiler

BrainfuckをJavaバイトコードにコンパイルします

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
// no ops, but push current position into queue  `LoopQueue.push(pos)`

// ]
goto `LoopQueue.poll()`
```

### tear down
```
return
```