package me.wux1an;

import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;

import java.io.*;

public class QuiteLogger extends PrintStreamLogger {
    public QuiteLogger() {
        super(new PrintStream(new NullOutputStream()));
    }

    static class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {

        }
    }
}
