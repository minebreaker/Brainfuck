package brainfuck;

import java.io.*;
import java.util.Arrays;

/*
https://ja.wikipedia.org/wiki/Brainfuck
処理系は次の要素から成る:
    Brainfuckプログラム、
    インストラクションポインタ（プログラム中のある文字を指す）、
    少なくとも30000個の要素を持つバイトの配列（各要素はゼロで初期化される）、
    データポインタ（前述の配列のどれかの要素を指す。最も左の要素を指すよう初期化される）、
    入力と出力の2つのバイトストリーム。

> ポインタをインクリメントする。ポインタをptrとすると、C言語の「ptr++;」に相当する。
< ポインタをデクリメントする。C言語の「ptr--;」に相当。
+ ポインタが指す値をインクリメントする。C言語の「(*ptr)++;」に相当。
- ポインタが指す値をデクリメントする。C言語の「(*ptr)--;」に相当。
. ポインタが指す値を出力に書き出す。C言語の「putchar(*ptr);」に相当。
, 入力から1バイト読み込んで、ポインタが指す先に代入する。C言語の「*ptr=getchar();」に相当。
[ ポインタが指す値が0なら、対応する ] の直後にジャンプする。C言語の「while(*ptr){」に相当。
] ポインタが指す値が0でないなら、対応する [ （の直後[1]）にジャンプする。C言語の「}」に相当[2]。

======================

独自命令
? ヘルプ
\ 終了
! 初期化
# dump
 */

public final class Interpreter {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Interpreter i = new Interpreter(System.out, System.in);

        System.out.println("Brainfuck Interpreter");
        System.out.print("> ");
        while (true) {
            String l = reader.readLine();
            if (l == null) break;
            if (l.isEmpty()) continue;
            if (i.interpret(l)) break;
            System.out.print("\n> ");
        }
        System.out.println("Good bye.");
    }

    private final PrintStream out;
    private final InputStream in;
    private final int[] var;
    private int p;

    public Interpreter(OutputStream out, InputStream in) {
        this(out, in, 30000);
    }

    /**
     * 新しいBrainfuckインタープリターを作成します.
     *
     * @param out     "."命令で使用される出力ストリーム
     * @param in      ","命令で使用される入力ストリーム
     * @param bufSize 処理に使用される配列のサイズ. デフォルトは30000.
     */
    public Interpreter(OutputStream out, InputStream in, int bufSize) {
        this.out = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
        this.in = in;
        this.var = new int[bufSize];
    }

    /**
     * 与えられたコードを処理します.
     * 入出力命令はコンストラクターで与えられたストリームに渡されます.
     *
     * @param line 解析対象のBrainfuckコードを表すnullでない文字列
     * @return 終了命令が読み込まれた場合、true. それ以外の場合false
     * @throws IOException 入出力にエラーが発生した場合
     */
    public boolean interpret(String line) throws IOException {
        char[] l = line.toCharArray();

        for (int i = 0 ; i < l.length ; i++) {
            char c = l[i];

            switch (c) {
            case '\\':
                return true;
            case '?':
                help();
                return false;
            case '!':
                init();
                return false;
            case '#':
                dump(c, l, i);
                break;
            case '>':
                p++;
                if (p == var.length) throw new IllegalStateException(String.format("Data pointer overflow : %d", i));
                break;
            case '<':
                if (p == 0) throw new IllegalStateException(String.format("Data pointer underflow : %d", i));
                p--;
                break;
            case '+':
                if (var[p] == Integer.MAX_VALUE) throw new IllegalStateException(String.format("Number overflow : %d", i));
                var[p]++;
                break;
            case '-':
                if (var[p] == Integer.MIN_VALUE) throw new IllegalStateException(String.format("Number underflow : %d", i));
                var[p]--;
                break;
            case '.':
                out.print((char) var[p]);
                break;
            case ',':
                var[p] = in.read();
                break;
            case '[':
                if (var[p] == 0) {
                    int cnt = -1;
                    while (l[i] != ']' || cnt != 0) {
                        if (l[i] == '[') cnt++;
                        if (l[i] == ']') cnt--;
                        i++;
                    }
                }
                break;
            case ']':
                if (var[p] != 0) {
                    int cnt = -1;
                    while (l[i] != '[' || cnt != 0) {
                        if (l[i] == ']') cnt++;
                        if (l[i] == '[') cnt--;
                        i--;
                    }
                }
                break;
            default:
                // do nothing
            }
        }

        return false;
    }

    /**
     * バッファーを初期化します.
     * スレッドセーフではありません.
     */
    public void init() {
        Arrays.fill(var, 0);
        p = 0;
    }

    private void help() {
        System.err.print("Brainfuck Interpreter\n" +
                "> ポインタをインクリメントする。ポインタをptrとすると、C言語の「ptr++;」に相当する。\n" +
                "< ポインタをデクリメントする。C言語の「ptr--;」に相当。\n" +
                "+ ポインタが指す値をインクリメントする。C言語の「(*ptr)++;」に相当。\n" +
                "- ポインタが指す値をデクリメントする。C言語の「(*ptr)--;」に相当。\n" +
                ". ポインタが指す値を出力に書き出す。C言語の「putchar(*ptr);」に相当。\n" +
                ", 入力から1バイト読み込んで、ポインタが指す先に代入する。C言語の「*ptr=getchar();」に相当。\n" +
                "[ ポインタが指す値が0なら、対応する ] の直後にジャンプする。C言語の「while(*ptr){」に相当。\n" +
                "] ポインタが指す値が0でないなら、対応する [ （の直後[1]）にジャンプする。C言語の「}」に相当[2]。\n" +
                "? ヘルプ\n" +
                "\\ 終了\n" +
                "! 初期化\n" +
                "# dump\n"
        );
    }

    private void dump(char c, char[] line, int ip) {
        System.err.println("Line:  " + String.valueOf(line));
        System.err.println("C:     " + c);
        System.err.println("IP:    " + ip);
        System.err.println("Var:   " + Arrays.toString(var));
        System.err.println("P:     " + p);
    }

}
