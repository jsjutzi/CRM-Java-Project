package View_Controller;

import Model.Customer;
import Model.User;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.sql.*;

public class AddModifyCustomerController {
    private boolean isModifyScreen = false;
    private int customerId = -1;
    private int addressId = -1;
    private int cityId = -1;
    private int countryId = -1;

    @FXML
    Label customerTitle;

    @FXML
    TextField customerIdTextField;

    @FXML
    TextField customerNameTextField;

    @FXML
    TextField customerAddressTextField;

    @FXML
    TextField customerAddress2TextField;

    @FXML
    TextField customerCityTextField;

    @FXML
    TextField customerZipCodeTextField;

    @FXML
    TextField customerPhoneTextField;

    @FXML
    TextField customerCountryTextField;

    @FXML
    RadioButton activeUser;

    @FXML
    RadioButton inactiveUser;

    public void loadSelectedCustomer(Customer selectedCustomer) {
        isModifyScreen = true;
        customerTitle.setText("Modify Customer Data");

        // Initialize fields to current data
        if (selectedCustomer.getActive() == 1) {
            activeUser.setSelected(true);
        } else {
            inactiveUser.setSelected(true);
        }

        customerIdTextField.setText(Integer.toString(selectedCustomer.getCustomerId()));
        customerNameTextField.setText(selectedCustomer.getCustomerName());
        customerAddressTextField.setText(selectedCustomer.getAddress());
        customerAddress2TextField.setText(selectedCustomer.getAddress2());
        customerCityTextField.setText(selectedCustomer.getCity());
        customerZipCodeTextField.setText(selectedCustomer.getPostalCode());
        customerPhoneTextField.setText(selectedCustomer.getPhone());
        customerCountryTextField.setText(selectedCustomer.getCountry());
        this.addressId = selectedCustomer.getAddressId();
        this.cityId = selectedCustomer.getAddressId();
        this.countryId = selectedCustomer.getAddressId();
        this.customerId = selectedCustomer.getCustomerId();
    }

    public void onCancel(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main_screen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void onSave(ActionEvent event) throws Exception {
        boolean isValidCustomer = validateCustomer();

        if (isValidCustomer) {
            if (isModifyScreen) {
                updateCustomer(event);
            } else {
                addNewCustomer(event);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("You must fill out all required fields to save this record.");
            alert.showAndWait();
        }
    }

    private boolean validateCustomer() {
        // Verify all required fields have values
        String customerName = customerNameTextField.getText();
        String address = customerAddressTextField.getText();
        String city = customerCityTextField.getText();
        String postalCode = customerZipCodeTextField.getText();
        String country = customerCountryTextField.getText();
        String phone = customerPhoneTextField.getText();

        boolean isValid = customerName != null
                && address != null
                && city != null
                && postalCode != null
                && country != null
                && phone != null
                && customerName.length() != 0
                && address.length() != 0
                && city.length() != 0
                && postalCode.length() != 0
                && country.length() != 0
                && phone.length() != 0;

        return isValid;
    }

    private void addNewCustomer(ActionEvent event) throws IOException, SQLException {
        String customerName = customerNameTextField.getText();
        String address = customerAddressTextField.getText();
        String address2 = customerAddress2TextField.getText();
        String city = customerCityTextField.getText();
        String postalCode = customerZipCodeTextField.getText();
        String country = customerCountryTextField.getText();
        String phone = customerPhoneTextField.getText();
        int active = activeUser.selectedProperty().getValue() ? 1 : 0;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        int countryId = -1;
        int cityId = -1;
        int addressId = -1;

        // Get user data and time data
        String username = User.getUsername();

        Connection conn = DBConnection.startConnection();
        String addCountryStatement = "INSERT INTO country (country, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?);";
        String addCityStatement = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?, ?, ?, ?, ?, ?);";
        String addAddressStatement = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String addCustomerStatement = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?, ?, ?, ?, ?, ?, ?);";

        DBQuery.setPreparedStatement(conn, addCountryStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Add new country
        ps.setString(1, country);
        ps.setTimestamp(2, timestamp);
        ps.setString(3, username);
        ps.setTimestamp(4, timestamp);
        ps.setString(5, username);
        ps.execute();

        // Must get country id from previous call
        String getCountry = "SELECT * FROM country WHERE country = \"" + country + "\";";

        ps.execute(getCountry);
        ResultSet rs = ps.getResultSet();


        if (rs.next()) {
            countryId = rs.getInt("countryId");
        }

        // Update city parameters
        DBQuery.setPreparedStatement(conn, addCityStatement);
        ps = DBQuery.getPreparedStatement();

        ps.setString(1, city);
        ps.setInt(2, countryId);
        ps.setTimestamp(3, timestamp);
        ps.setString(4, username);
        ps.setTimestamp(5, timestamp);
        ps.setString(6, username);
        ps.execute();

        // Must get city id from previous call
        String getCity = "SELECT * FROM city WHERE city = \"" + city + "\";";
        ps.execute(getCity);
        rs = ps.getResultSet();


        if (rs.next()) {
            cityId = rs.getInt("cityId");
        }

        // Update address parameters
        DBQuery.setPreparedStatement(conn, addAddressStatement);
        ps = DBQuery.getPreparedStatement();

        ps.setString(1, address);
        ps.setString(2, address2);
        ps.setInt(3, cityId);
        ps.setString(4, postalCode);
        ps.setString(5, phone);
        ps.setTimestamp(6, timestamp);
        ps.setString(7, username);
        ps.setTimestamp(8, timestamp);
        ps.setString(9, username);
        ps.execute();


        // Must get address id from prev call
        String getAddress = "SELECT * FROM address WHERE address = \"" + address + "\";";
        ps.execute(getAddress);
        rs = ps.getResultSet();


        if (rs.next()) {
            addressId = rs.getInt("addressId");
        }

        // Update Customer parameters
        DBQuery.setPreparedStatement(conn, addCustomerStatement);
        ps = DBQuery.getPreparedStatement();
        ps.setString(1, customerName);
        ps.setInt(2, addressId);
        ps.setInt(3, active);
        ps.setTimestamp(4, timestamp);
        ps.setString(5, username);
        ps.setTimestamp(6, timestamp);
        ps.setString(7, username);

        ps.execute();
        confirmSave(event);
    }

    private void updateCustomer(ActionEvent event) throws IOException, SQLException {
        String customerName = customerNameTextField.getText();
        String address = customerAddressTextField.getText();
        String address2 = customerAddress2TextField.getText();
        String city = customerCityTextField.getText();
        String postalCode = customerZipCodeTextField.getText();
        String country = customerCountryTextField.getText();
        String phone = customerPhoneTextField.getText();
        int active = activeUser.selectedProperty().getValue() ? 1 : 0;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Get user data and time data
        int userId = User.getUserId();
        String username = User.getUsername();
        System.out.println("username is : " + username);

        Connection conn = DBConnection.startConnection();
        String updateCountryStatement = "UPDATE country SET country = ?, lastUpdate = ?, lastUpdateBy = ? WHERE countryId = ?;";
        String updateCityStatement = "UPDATE city SET city = ?, lastUpdate = ?, lastUpdateBy = ? WHERE cityId = ?;";
        String updateAddressStatement = "UPDATE address SET address = ?, address2 = ?, postalCode = ?, phone = ?, lastUpdate = ?, lastUpdateBy = ? WHERE addressId = ?;";
        String updateCustomerStatement = "UPDATE customer SET customerName = ?, active = ?, lastUpdate = ?, lastUpdateBy = ? WHERE customerId = ?;";

        DBQuery.setPreparedStatement(conn, updateCountryStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Update country parameters
        ps.setString(1, country);
        ps.setTimestamp(2, timestamp);
        ps.setString(3, username);
        ps.setInt(4, countryId);
        ps.execute();

        // Update city parameters
        DBQuery.setPreparedStatement(conn, updateCityStatement);
        ps = DBQuery.getPreparedStatement();

        ps.setString(1, city);
        ps.setTimestamp(2, timestamp);
        ps.setString(3, username);
        ps.setInt(4, cityId);
        ps.execute();

        // Update address parameters
        DBQuery.setPreparedStatement(conn, updateAddressStatement);
        ps = DBQuery.getPreparedStatement();
        ps.setString(1, address);
        ps.setString(2, address2);
        ps.setString(3, postalCode);
        ps.setString(4, phone);
        ps.setTimestamp(5, timestamp);
        ps.setString(6, username);
        ps.setInt(7, addressId);
        ps.execute();

        // Update Customer parameters
        DBQuery.setPreparedStatement(conn, updateCustomerStatement);
        ps = DBQuery.getPreparedStatement();
        ps.setString(1, customerName);
        ps.setInt(2, active);
        ps.setTimestamp(3, timestamp);
        ps.setString(4, username);
        ps.setInt(5, customerId);
        ps.execute();

        confirmSave(event);
    }

    private void confirmSave(ActionEvent event) throws IOException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Records Update");
        alert.setContentText("Customer Record was saved successfully");
        alert.showAndWait();

        // Return to main screen

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main_screen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
