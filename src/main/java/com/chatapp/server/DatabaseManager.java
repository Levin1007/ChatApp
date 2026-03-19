package com.chatapp.server;

import java.sql.*;

/**
 * DatabaseManager handles all communication with the H2 embedded database.
 * It creates the database file on first run and provides a shared connection method.
 */
public class DatabaseManager {

    // Saves the database as a file named chatapp_db.mv.db in the project root.
    // AUTO_SERVER=TRUE allows multiple connections (needed when running 2 clients).
    private static final String DB_URL      = "jdbc:h2:./chatapp_db;AUTO_SERVER=TRUE";
    private static final String DB_USER     = "sa";
    private static final String DB_PASSWORD = "";

    /** Opens and returns a JDBC connection to the H2 database. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Creates the users and messages tables if they don't already exist.
     * Call this once when the server starts.
     */
    public static void initializeDatabase() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(50)  UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL
            );
            """;

        String createMessages = """
            CREATE TABLE IF NOT EXISTS messages (
                id          INTEGER   PRIMARY KEY AUTO_INCREMENT,
                sender_id   INTEGER   NOT NULL,
                receiver_id INTEGER   NOT NULL,
                content     TEXT      NOT NULL,
                timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (sender_id)   REFERENCES users(id),
                FOREIGN KEY (receiver_id) REFERENCES users(id)
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement()) {

            stmt.execute(createUsers);
            stmt.execute(createMessages);
            System.out.println("[DB] Tables ready.");

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Could not initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
