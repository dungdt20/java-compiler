package compiler;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

enum TokenType {
    EXIT,
    INT,
    SEMICOLON
}

record Token (TokenType token, String value) {}

public class Main {
    public static void main(String[] args) throws IOException {
        List<Token> tokens;
        try (FileInputStream input = new FileInputStream("./resource/in.of")) {
            tokens = tokenize(input);
        }

        String out = parse(tokens);

        FileWriter fileWriter = new FileWriter("./build/out.asm");
        fileWriter.write(out);
        fileWriter.close();

        Runtime runTime = Runtime.getRuntime();
        runTime.exec("nasm -f elf64 ./build/out.asm");
        runTime.exec("ld -s -o ./build/out ./build/out.o");
    }

    private static List<Token> tokenize(FileInputStream input) throws IOException {
        List<Token> tokens = new ArrayList<>();
        StringBuilder tokenBuffer = new StringBuilder();
        int data = input.read();

        while (data != -1) {
            if (Character.isLetter(data)) {
                while (Character.isLetter(data)) {
                    tokenBuffer.append((char) data);
                    data = input.read();
                }

                String token = tokenBuffer.toString();
                if (token.compareTo("exit") == 0) {
                    tokens.add(new Token(TokenType.EXIT, token));
                } else {
                    System.err.println("Wrong onlyFan syntax");
                    System.exit(1);
                }
                tokenBuffer.delete(0, tokenBuffer.length());
            }

            if (Character.isDigit(data)) {
                while (Character.isDigit(data)) {
                    tokenBuffer.append((char) data);
                    data = input.read();
                }

                String token = tokenBuffer.toString();
                tokens.add(new Token(TokenType.INT, token));
                tokenBuffer.delete(0, tokenBuffer.length());
            }

            if ((char) data == ';') {
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
                data = input.read();
            }

            if (Character.isSpaceChar(data)) {
                data = input.read();
            }
        }
        return tokens;
    }

    private static String parse(List<Token> tokens) {
        StringBuilder out = new StringBuilder();
        out.append("global _start\n");
        out.append("_start:\n");

        for (int i = 0; i < tokens.size() - 2; i++) {
            if (tokens.get(i).token() == TokenType.EXIT) {
                out.append("\tmov rax, 60\n");
                if (tokens.get(i + 1).token() == TokenType.INT) {
                    out.append("\tmov rdi, ").append(tokens.get(i + 1).value()).append("\n");
                    if (tokens.get(i + 2).token() == TokenType.SEMICOLON) {
                        out.append("\tsyscall\n");
                    }
                }
            }
        }
        for (Token token : tokens) {
            if (token.token() == TokenType.EXIT) {
                out.append("\tmov rax, 60\n");
            }
            if (token.token() == TokenType.INT) {
                out.append("\tmov rdi, ").append(token.value()).append("\n");
            }
            if (token.token() == TokenType.SEMICOLON) {
                out.append("\tsyscall\n");
            }
        }

        return out.toString();
    }
}