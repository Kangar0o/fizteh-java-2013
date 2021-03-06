package ru.fizteh.fivt.students.eltyshev.parallel.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeTableProviderTest {
    private static final String DATABASE_DIRECTORY = "C:\\temp\\storeable_test";
    private static final String NEW_TABLE_NAME = "newTable";
    private static final int THREADS_COUNT = 5;
    private TableProvider provider;
    private AtomicInteger counter = new AtomicInteger(0);

    private Table newTable;

    @Before
    public void setUp() throws Exception {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        provider = factory.create(DATABASE_DIRECTORY);
    }

    @After
    public void tearDown() throws Exception {
        if (provider.getTable(NEW_TABLE_NAME) != null) {
            provider.removeTable(NEW_TABLE_NAME);
        }
        if (provider.getTable("randomName") != null) {
            provider.removeTable("randomName");
        }
        provider = null;
    }

    @Test
    public void testCreateTable() throws Exception {
        List<Thread> threads = new ArrayList<Thread>();
        for (int threadNumber = 0; threadNumber < THREADS_COUNT; ++threadNumber) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    createTableAndCount();
                }
            }));
            threads.get(threadNumber).start();
        }
        createTableAndCount();
        for (Thread thread : threads) {
            thread.join();
        }
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testOneCreateEveryoneGet() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createTable();
            }
        });
        thread.join();
        for (int index = 0; index < THREADS_COUNT; ++index) {
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(newTable, provider.getTable("randomName"));
                }
            });
            thread1.start();

            Assert.assertEquals(newTable, provider.getTable("randomName"));
        }
    }

    private void createTable() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        try {
            newTable = provider.createTable("randomName", columnTypes);
        } catch (IOException e) {
            //
        }
    }

    private void createTableAndCount() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(500) + 1000);
        } catch (InterruptedException e) {
            return;
        }
        try {
            if (provider.createTable(NEW_TABLE_NAME, columnTypes) != null) {
                counter.getAndIncrement();
            }
        } catch (IOException e) {
            //
        }
    }
}
