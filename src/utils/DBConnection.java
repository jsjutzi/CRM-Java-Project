package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // JDBC url string parts
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/U0689d";

    // JDBC url connection string
    private static final String jdbcUrl = protocol.concat(vendorName).concat(ipAddress);

    // Driver and connection interface reference
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    // Username and password credentials
    private static final String username = "U0689d";
    private static final String password = "53688697425";

    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connection Opened");
        }
        catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed");
        }
        catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}