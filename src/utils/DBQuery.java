package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBQuery {
    private static PreparedStatement statement;
    // Create Prepared Statement Object
    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        statement = conn.prepareStatement(sqlStatement);
    }

    // Return Prepared Statement Object
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
}
