package View_Controller;

import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBQuery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Locale;

public class LoginScreenController {
    private String userLanguage = Locale.getDefault().getLanguage();
    private String errorTitle;
    private String errorHeader;
    private String errorContent;
    private String errorContent2;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Button loginButton;



    public void initialize() {
        if (userLanguage == "fr") {
            usernameLabel.setText("Nom d'utilisateur");
            passwordLabel.setText("Mot de passe");
            loginButton.setText("S'identifier");

            errorTitle = "Mauvaise connexion";
            errorHeader = "Les informations d'identification invalides";
            errorContent = "La combinaison nom d'utilisateur / mot de passe que vous avez saisie n'est pas valide";
            errorContent2 = "Vous devez entrer un nom d'utilisateur et un mot de passe pour vous connecter";

        } else {
            usernameLabel.setText("Username");
            passwordLabel.setText("Password");
            loginButton.setText("Login");

            errorTitle = "Bad Login";
            errorHeader = "Invalid Credentials";
            errorContent = "The username/password combination you entered is invalid.";
            errorContent2 = "You must enter both a username and password to login";
        }
    }
    public void onLogin(ActionEvent event) throws SQLException, IOException {
        if (username.getText() == null || password.getText() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(errorTitle);
            alert.setHeaderText(errorHeader);
            alert.setContentText(errorContent2);
            alert.showAndWait();
        } else {
            PreparedStatement statement = DBQuery.getPreparedStatement();

            String selectStatement = "SELECT * FROM user WHERE username = \"" + username.getText() + "\" AND password = \"" + password.getText() + "\";";
            statement.execute(selectStatement);
            ResultSet rs = statement.getResultSet();

            if (rs.next()) {
                int userId = rs.getInt("userId");

                User user = new User(userId, username.getText(), password.getText());

                LocalDateTime now = LocalDateTime.now();
                User.setLocalDateTime(now);
                Timestamp logTime = Timestamp.valueOf(now);

                // Record login in text file here:
                String filename = "src/log.txt", item;

                FileWriter fwriter = new FileWriter(filename, true);
                PrintWriter outputFile = new PrintWriter(fwriter);

                outputFile.println(username.getText() + ": " + logTime.toString() + "\n");
                outputFile.close();
                
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("main_screen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(errorTitle);
                alert.setHeaderText(errorHeader);
                alert.setContentText(errorContent);
                alert.showAndWait();
            }
        }
    }
}
