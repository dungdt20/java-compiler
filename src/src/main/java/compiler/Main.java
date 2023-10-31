package compiler;

import compiler.Structure.NodeExit;
import compiler.Structure.Token;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser();
        Generator generator = new Generator();

        File file = new File("./resource/in.of");
        char[] cbuf = new char[(int) file.length()];

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            int charsRead = bufferedReader.read(cbuf, 0, cbuf.length);
        }

        List<Token> tokens = tokenizer.tokenize(cbuf);
        NodeExit parsed = parser.parse(tokens);
        String out = generator.generate(parsed);

        build(out);
    }

    private static void build(String out) throws IOException {
        FileWriter fileWriter = new FileWriter("./build/out.asm");
        fileWriter.write(out);
        fileWriter.close();

        Runtime runTime = Runtime.getRuntime();
        runTime.exec("nasm -f elf64 ./build/out.asm");
        runTime.exec("ld -s -o ./build/out ./build/out.o");
    }
}