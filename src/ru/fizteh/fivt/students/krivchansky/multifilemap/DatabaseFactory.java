package ru.fizteh.fivt.students.krivchansky.multifilemap;


import java.io.File;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;


public class DatabaseFactory implements TableProviderFactory {
    public TableProvider create(String directory) {
    	if (directory == null || directory.isEmpty() ) {
    		throw new IllegalArgumentException ("directory name cannot be null");
    	}
    	File databaseDirectory = new File(directory);
    	if (databaseDirectory.isFile()) {
    		throw new IllegalArgumentException ("it must be directory, not file");
    	}
    	if (!databaseDirectory.exists()) {
    		databaseDirectory.mkdir();
    	}
        return new Database(databaseDirectory.getAbsolutePath());
    }
}
