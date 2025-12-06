package fr.kainovaii.guardian.core.database;

import org.javalite.activejdbc.Base;

import java.io.File;
import java.util.logging.Logger;

public class SQLite
{
    private static SQLite instance;
    private final File dataFolder;
    private final Logger logger;

    public SQLite(Logger logger
    ) {
        this.logger = logger;
        this.dataFolder = new File("Guardian");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public static SQLite getInstance(Logger logger)
    {
        if (instance == null) {
            instance = new SQLite(logger);
        }
        return instance;
    }

    public void connectDatabaseForCurrentThread()
    {
        if (!Base.hasConnection()) {
            try {
                File dbFile = new File(dataFolder, "data.db");
                if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();

                String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                Base.open("org.sqlite.JDBC", url, "", "");
                logger.info("SQLite connection open for the thread : " + Thread.currentThread().getName());
            } catch (Exception e) {
                logger.severe("SQLite error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void ensureTablesExist()
    {
        if (!Base.hasConnection()) throw new IllegalStateException("No SQLite connection open!");

        Base.exec("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT NOT NULL
        )
        """);

        Base.exec("""
        CREATE TABLE IF NOT EXISTS alerts (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id TEXT NOT NULL,
            member_name TEXT NOT NULL,
            word TEXT NOT NULL,
            message TEXT NOT NULL,
            channel TEXT NOT NULL,
            timestamp TEXT NOT NULL,
            toxicity REAL NOT NULL
        )
        """);

        Base.exec("""
        CREATE TABLE IF NOT EXISTS penalty (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id TEXT NOT NULL,
            member_name TEXT NOT NULL,
            reason TEXT NOT NULL,
            author TEXT NOT NULL,
            timestamp TEXT NOT NULL,
            type TEXT NOT NULL,
            status TEXT NOT NULL,
            toxicity REAL NOT NULL
        )
        """);

        logger.info("Tables SQLite créées ou existantes vérifiées.");
    }

    public void close() {
        if (Base.hasConnection()) Base.close();
    }
}
