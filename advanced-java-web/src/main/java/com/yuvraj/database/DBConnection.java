
package com.yuvraj.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/college_vehicle_booking";

    private static final String USERNAME = "root";

    // Temporary: replace with your local password.
    // We will move this into a secure configuration file next.
    private static final String PASSWORD = "root123";

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
            URL, USERNAME, PASSWORD
        );
    }
}
