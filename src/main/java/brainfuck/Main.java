package brainfuck;

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

? ヘルプ
\ 終了
! 初期化

hello world
>+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.[-]>++++++++[<++++>-]<.>+++++++++++[<+++++>-]<.>++++++++[<+++>-]<.+++.------.--------.[-]>++++++++[<++++>-]<+.[-]++++++++++.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

    private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    private static int[] var = new int[30000];
    private static int p;

    public static void main(String[] args) throws IOException {
        interpret();
    }

    private static void interpret() throws IOException {
        while (true) {
            char[] l = stdin.readLine().toCharArray();

            for (int i = 0; i < l.length; i++) {
                char c = l[i];

//                dump(c, l, dStack, dP);

                switch (c) {
                case '\\':
                    return;
                case '?':
                    help();
                    break;
                case '>':
                    p++;
                    break;
                case '<':
                    if (p == 0) throw new IllegalStateException("Data pointer underflow.");
                    p--;
                    break;
                case '+':
                    var[p]++;
                    break;
                case '-':
                    var[p]--;
                    break;
                case '.':
                    System.out.print((char) var[p]);
                    break;
                case ',':
                    var[p] = System.in.read();
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
        }
    }

    private static void help() {
        System.out.println("Brainfuck Interpreter");
    }

    private static void dump(char c, char[] line, int[] stack, int dp) {
        System.out.println("Line:  " + String.valueOf(line));
        System.out.println("Var:   " + Arrays.toString(var));
        System.out.println("C:     " + c);
        System.out.println("P:     " + p);
        System.out.println("Stack: " + Arrays.toString(stack));
        System.out.println("DP:    " + dp);
    }

}
