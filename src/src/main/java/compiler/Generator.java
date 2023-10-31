package compiler;

import compiler.Structure.NodeExit;

public class Generator {

    public String generate(NodeExit root) {
        StringBuilder out = new StringBuilder();
        out.append("global _start\n");
        out.append("_start:\n");
        out.append("\tmov rax, 60\n");
        out.append("\tmov rdi, ").append(root.expr().intLit().value()).append("\n");
        out.append("\tsyscall\n");

        return out.toString();
    }
}
