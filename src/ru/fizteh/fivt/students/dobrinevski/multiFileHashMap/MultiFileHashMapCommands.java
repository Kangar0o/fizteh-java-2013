package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.fizteh.fivt.students.dobrinevski.shell.Command;
import ru.fizteh.fivt.students.dobrinevski.shell.Shell;

public class MultiFileHashMapCommands {

    public static class Put extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable == null) {
                System.out.println("no table");
                return;
            }
            Integer hashCode = args[1].hashCode();
            hashCode = Math.abs(hashCode);
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;

            File dbFile = new File(parent.curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                    + File.separator + nFile.toString() + ".dat");
            if (dbFile.exists() && dbFile.isFile()) {
                parent.parseFile(dbFile, nDirectory, nFile);
            }
            String value = parent.dataBase.get(nDirectory * 16 + nFile).put(args[1], args[2]);
            System.out.println(value == null ? "new" : "overwrite\n" + value);

            }
        Put() {
            super(3);
        }
    }

    public static class Get extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable == null) {
                System.out.println("no table");
                return;
            }
            Integer hashCode = args.hashCode();
            hashCode = Math.abs(hashCode);
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;

            File dbFile = new File(parent.curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                    + File.separator + nFile.toString() + ".dat");
            if (dbFile.exists() && dbFile.isFile()) {
                parent.parseFile(dbFile, nDirectory, nFile);
            }
            String value = parent.dataBase.get(nDirectory * 16 + nFile).get(args);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        }
        Get() {
            super(2);
        }
    }

    public static class Use extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            Path dbsDir = Paths.get(System.getProperty("user.dir")).resolve(args[1]).normalize();
            if (Files.notExists(dbsDir) || !Files.isDirectory(dbsDir)) {
                System.out.println(args[1] + " not exists");
                return;
            }
            if (parent.curTable != null) {
                parent.writeOut();
            }
            parent.curTable = new File(System.getProperty("user.dir") + File.separator + args[1]);
            System.out.println("using " + args[1]);
        }
        Use() {
            super(2);
        }
    }

    public static class Drop extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable != null && parent.curTable.getCanonicalPath().toString()
                    .equals(parentShell.currentDir + File.separator + args[1])) {
                parent.writeOut();
                parent.curTable = null;
            }
            Path pathToRemove = parentShell.currentDir.toPath().resolve(args[1]).normalize();
            if (!Files.exists(pathToRemove)) {
                System.out.println(args[1] + " not exists");
                return;
            }
            File fileToRemove = new File(args[1]);
            if (!fileToRemove.isAbsolute()) {
                fileToRemove = new File(parentShell.currentDir.getCanonicalPath() + File.separator + args[1]);
            }
            File[] filesToRemove = fileToRemove.listFiles();
            if (filesToRemove != null) {
                for (File file : filesToRemove) {
                    String[] toRemove = new String[2];
                    toRemove[0] = args[0];
                    toRemove[1] = file.getPath();
                    this.innerExecute(toRemove);
                    System.out.println("dropped");
                }
            }
        }
        Drop() {
            super(2);
        }
    }

    public static class Create extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            File tmpFile = new File(args[1]);
            if (!tmpFile.isAbsolute()) {
                tmpFile = new File(parentShell.currentDir.getCanonicalPath() + File.separator + args[1]);
            }
            if (tmpFile.exists() && tmpFile.isDirectory()) {
                    System.out.println(args[1] + " exists");
                    return;
                }
            if (!tmpFile.mkdir()) {
                    throw new Exception("\'" + args[1] + "\': Table wasn't created");
                }
            System.out.println("created");
        }
        Create() {
            super(2);
        }
    }

    public static class Remove extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
            if (parent.curTable == null) {
                System.out.println("no table");
                return;
            }
            Integer hashCode = args.hashCode();
            hashCode = Math.abs(hashCode);
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;

            File dbFile = new File(parent.curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                    + File.separator + nFile.toString() + ".dat");
            if (dbFile.exists() && dbFile.isFile()) {
                parent.parseFile(dbFile, nDirectory, nFile);
            }
            if (parent.dataBase.get(nDirectory * 16 + nFile).remove(args) == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
        Remove() {
            super(2);
        }
    }

    public static class Exit extends MultiFileHashMapCommand {
        @Override
        public void innerExecute(String[] args) throws Exception {
        parent.writeOut();
        }
        Exit() {
            super(1);
        }
    }


}
