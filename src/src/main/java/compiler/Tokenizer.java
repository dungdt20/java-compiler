package compiler;

import compiler.Structure.Token;
import compiler.Structure.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Tokenizer {

    private char[] input;

    private int index;

    public List<Token> tokenize(char[] input) {
        this.resetIndex();
        this.setInput(input);

        try {
            return this.process();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Token> process() throws Exception {

        List<Token> tokens = new ArrayList<>();
        StringBuilder tokenBuffer = new StringBuilder();

        while (peak().isPresent()) {
            char data = consume();

            if (Character.isLetter(data)) {
                while (Character.isLetter(data)) {
                    tokenBuffer.append(data);

                    if (peak().isEmpty()) {
                        break;
                    }

                    data = consume();
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
                    tokenBuffer.append(data);

                    if (peak().isEmpty()) {
                        break;
                    }

                    data = consume();
                }

                String token = tokenBuffer.toString();
                tokens.add(new Token(TokenType.INT, token));
                tokenBuffer.delete(0, tokenBuffer.length());
            }

            if (data == ';') {
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
            }
        }

        this.resetIndex();
        return tokens;
    }

    private void resetIndex() { this.index = 0; }

    private void setInput(char[] input) {
        this.input = input;
    }

    private char consume() throws Exception {
        if (this.index >= this.input.length) {
            throw new Exception("Can not consume when out scope");
        }

        return this.input[this.index++];
    }

    private Optional<Character> peak() {
        if (this.index >= this.input.length) {
            return Optional.empty();
        }

        return Optional.of(this.input[index]);
    }
}
