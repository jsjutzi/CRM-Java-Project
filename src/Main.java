import Model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View_Controller/main_screen.fxml"));
        Scene scene = new Scene(root);
        // Hard code testing to skip login during dev work
        User user = new User(1, "test", "test");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws SQLException {
        // Establish DB Connection
        Connection conn = DBConnection.startConnection();
        String selectStatement = "SELECT * FROM country";

        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.execute();

        ResultSet rs = ps.getResultSet();
         while(rs.next()) {
             int countryId = rs.getInt("countryId");
             String countryName = rs.getString("country");
             LocalDate date = rs.getDate("createDate").toLocalDate();
             LocalTime time = rs.getTime("createDate").toLocalTime();
             String createdBy = rs.getString("createdBy");
             LocalDateTime lastUpdate = rs.getTimestamp("lastUpdate").toLocalDateTime();

             // Display Record For Testing
             System.out.println(countryId + " | " + countryName + " | " + date + " | " + time + " | " + createdBy + " | " + lastUpdate);

         }


        launch(args);
        DBConnection.closeConnection();
    }
}
