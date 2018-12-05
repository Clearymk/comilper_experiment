package lexure;

import symbols.Type;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
    public static int line = 1;
    private char peek = ' ';
    private Hashtable<String, Word> words = new Hashtable<>();

    public Lexer() {
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        reserve(Word.True);
        reserve(Word.False);
        reserve(Type.Int);
        reserve(Type.Char);
        reserve(Type.Bool);
        reserve(Type.Float);
    }

    private void reserve(Word word) {
        words.put(word.lexeme, word);
    }

    private void readch() throws IOException {
        peek = (char) System.in.read();
    }

    private boolean readch(char c) throws IOException {
        peek = (char) System.in.read();

        if (peek == c) {
            return true;
        } else {
            peek = ' ';
        }

        return false;
    }

    public Token scan() throws IOException {
        //略过空格，换行
        for (; ; readch()) {
            if (peek == ' ' || peek == '\t') {
                continue;
            } else if (peek == '\n') {
                line++;
            } else {
                break;
            }
        }
        //判断复合词法单元
        switch (peek) {
            case '&':
                if (readch('&')) {
                    showToken(Word.and.tag, Word.and.lexeme);
                    return Word.and;
                } else {
                    return newToken('&');
                }
            case '|':
                if (readch('|')) {
                    showToken(Word.or.tag, Word.or.lexeme);
                    return Word.or;
                } else {
                    return newToken('|');
                }
            case '=':
                if (readch('=')) {
                    showToken(Word.eq.tag, Word.eq.lexeme);
                    return Word.eq;
                } else {
                    return newToken('=');
                }
            case '!':
                if (readch('=')) {
                    showToken(Word.ne.tag, Word.ne.lexeme);
                    return Word.ne;
                } else {
                    return newToken('!');
                }
            case '>':
                if (readch('=')) {
                    return Word.ge;
                } else {
                    return newToken('>');
                }
            case '<':
                if (readch('=')) {
                    return Word.le;
                } else {
                    return newToken('<');
                }
        }
        //判断整型数
        if (Character.isDigit(peek)) {
            int i = 0;
            do {
                i = i * 10 + Character.digit(peek, 10);
                readch();
            } while (Character.isDigit(peek));

            if (peek != '.') {
                Num n = new Num(i);
                showToken(n.tag, n.value + "");
                return n;
            }
            //判断浮点数
            float d = 10;
            float j = i;

            do {
                j += (float) Character.digit(peek, 10) / d;
                d = d * 10;
            } while (Character.isDigit(peek));

            Real r = new Real(j);
            showToken(r.tag, r.value + "");
            return r;
        }
        //判断标识符或关键字
        if (Character.isLetter(peek)) {
            StringBuilder builder = new StringBuilder();

            do {
                builder.append(peek);
                readch();
            } while (Character.isLetter(peek));

            String s = builder.toString();
            Word w = words.get(s);

            if (w != null) {
                showToken(w.tag, w.lexeme);
                return w;
            }

            w = new Word(s, Tag.ID);
            showToken(w.tag, w.lexeme);

            words.put(s, w);
            return w;
        }

        Token token = new Token(peek);
        showToken(token.tag, peek + "");
        peek = ' ';
        return token;
    }

    private void showToken(int tag, String lexeme) {
        System.out.println("<" + tag + "," + lexeme + ">");
    }

    private Token newToken(char c) {
        Token t = new Token(c);
        showToken(t.tag, c + "");
        return t;
    }
}
