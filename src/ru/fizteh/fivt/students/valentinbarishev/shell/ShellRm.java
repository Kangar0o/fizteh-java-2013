package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellRm implements ShellCommand {
    private String name = "rm";
    private int numberOfParameters = 2;

    private Context context;
    private String[] args;

    public ShellRm(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.remove(args[1]);
        } catch (IOException e) {
            throw new InvalidCommandException(name + " argument " + args[1]
                    + " " + e.getMessage());
        }
    }

    @Override
    public boolean isMyCommand(final String[] command) {
        if (command[0].equals(name)) {
            if (command.length > numberOfParameters) {
                throw new InvalidCommandException(name + " too many arguments!");
            }
            if (command.length == 1) {
                throw new InvalidCommandException("Usage: " + name + " <file/dir>");
            }
            args = command;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfParameters() {
        return numberOfParameters;
    }
}
