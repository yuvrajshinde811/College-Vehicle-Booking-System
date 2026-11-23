package com.yuvraj.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection manages JDBC connections to the MySQL database.
 * Credentials are securely loaded from db.properties to avoid exposing secrets.
 */
public class DBConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String driverClassName;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                driverClassName = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                // Fallback to environment variables or local defaults
                url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/college_vehicle_booking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                username = System.getenv().getOrDefault("DB_USER", "root");
                password = System.getenv().getOrDefault("DB_PASSWORD", "root123");
                driverClassName = "com.mysql.cj.jdbc.Driver";
            }

            Class.forName(driverClassName);
        } catch (Exception e) {
            System.err.println("Failed to load database configuration or JDBC driver: " + e.getMessage());
        }
    }

    /**
     * Establishes and returns a new JDBC Connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (url == null || username == null) {
            loadConfiguration();
        }
        return DriverManager.getConnection(url, username, password);
    }
}

