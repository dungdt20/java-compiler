package compiler;

import compiler.Structure.NodeExit;
import compiler.Structure.NodeExpr;
import compiler.Structure.Token;
import compiler.Structure.TokenType;

import java.util.List;
import java.util.Optional;

public class Parser {

    private List<Token> tokens;

    private int index;

    public NodeExit parse(List<Token> tokens) {
        this.resetIndex();
        this.setTokens(tokens);

        try {
            Optional<NodeExit> process = this.process();
            if (process.isPresent()) {
                return process.get();
            }

            throw new Exception("Invalid expression");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<NodeExit> process() throws Exception {
        Optional<NodeExit> nodeExit = Optional.empty();

        while (peak().isPresent()) {
            Token token = consume();

            if (token.token() == TokenType.EXIT) {
                Optional<NodeExpr> nodeExpr = parseExpr();

                if (nodeExpr.isPresent()) {
                    nodeExit = Optional.of(new NodeExit(nodeExpr.get()));
                } else {
                    System.err.println("Invalid expression");
                    System.exit(1);
                }

                if (peak().isEmpty() || peak().get().token() != TokenType.SEMICOLON) {
                    System.err.println("Invalid expression");
                    System.exit(1);
                }
            }
        }

        return nodeExit;
    }

    private Optional<NodeExpr> parseExpr() throws Exception {
        if (peak().isPresent() && peak().get().token() == TokenType.INT) {
            return Optional.of(new NodeExpr(consume()));
        }

        return Optional.empty();
    }

    private void resetIndex() { this.index = 0; }

    private void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token consume() throws Exception {
        if (this.index >= this.tokens.size()) {
            throw new Exception("Can not consume when out scope");
        }

        return this.tokens.get(index++);
    }

    private Optional<Token> peak() {
        if (this.index >= this.tokens.size()) {
            return Optional.empty();
        }

        return Optional.of(this.tokens.get(index));
    }
}
