package com.adagency.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
    private static final String URL      = System.getProperty("db.url",      "jdbc:postgresql://localhost:5432/ad_agency");
    private static final String USER = System.getProperty("db.user", "user");
    private static final String PASSWORD = System.getProperty("db.password", "");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC драйвер не найден", e);
        }
    }

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}