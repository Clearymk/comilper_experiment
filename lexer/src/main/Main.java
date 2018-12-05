package main;

import lexure.Lexer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();

        while (true) {
            lexer.scan();
        }
    }
}
