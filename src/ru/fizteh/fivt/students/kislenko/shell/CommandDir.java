package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class CommandDir implements Command {
    public String getName() {
        return "dir";
    }

    public void run(State state, String[] empty) throws IOException {
        if (empty.length > 0) {
            throw new IOException("pwd: Too many arguments.");
        }
        PrintStream ps = new PrintStream(System.out);
        File currentDir = new File(state.getState().toString());
        File[] entries = currentDir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                ps.println(entry.getName());
            }
        }
    }
}