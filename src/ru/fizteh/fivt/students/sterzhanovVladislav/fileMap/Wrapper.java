package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.network.TelnetServerContext;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Command;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class Wrapper {
    
    public static void main(String[] args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.out.println("fizteh.db.dir not set");
            System.exit(-1);
        }
        try (DatabaseContext dbContext = new DatabaseContext(new FileMapProvider(dbDir));
                TelnetServerContext serverContext = new TelnetServerContext(dbDir)) {
            HashMap<String, Command> cmdMap = ShellUtility.initCmdMap(dbContext, serverContext);
            boolean isInteractiveMode = true;
            InputStream cmdStream = System.in;
            PrintStream errorStream = System.err;
            if (args.length > 0) {
                cmdStream = ShellUtility.createStream(args);
                errorStream = System.out;
                isInteractiveMode = false;
            }
            ShellUtility.execShell(cmdMap, cmdStream, System.out, errorStream, isInteractiveMode, isInteractiveMode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        System.exit(0);
    }
}
