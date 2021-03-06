package View_Controller;

import Model.Appointment;
import Model.Customer;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.sql.*;
import java.time.*;

public class AddModifyApptController {
    private ObservableList<String> allTypes = FXCollections.observableArrayList();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<String> allConsultants = FXCollections.observableArrayList();
    private boolean isModifyScreen = false;
    private int selectedAppointmentId;
    private String error = "";

    public ComboBox<Customer> customerComboBox;
    public ComboBox<String> typeComboBox;
    public ComboBox<String> consultantComboBox;
    public ComboBox<LocalTime> startComboBox;
    public ComboBox<LocalTime> endComboBox;
    public ComboBox<LocalDate> dateComboBox;

    @FXML
    TextField title;

    @FXML
    Label apptTitle;

    public void initialize() throws SQLException  {
        // Fill Types
        allTypes.add("Meet and Greet");
        allTypes.add("Check-in");
        allTypes.add("Advisory");
        allTypes.add("Other");

        // Fill Consultants
        allConsultants.add("Steven Tyler");
        allConsultants.add("Bryce Hull");
        allConsultants.add("Joe Warren");
        allConsultants.add("Dino Mandich");
        allConsultants.add("Jack Jutzi");

        // Get all customers
        PreparedStatement statement = DBQuery.getPreparedStatement();

        String selectCustomersStatement = "SELECT customerId, customerName FROM customer ORDER BY customerName;";

        statement.execute(selectCustomersStatement);
        ResultSet rs = statement.getResultSet();

        while (rs.next()) {
            Customer currentCustomer = new Customer();
            currentCustomer.setCustomerId(rs.getInt("customerId"));
            currentCustomer.setCustomerName(rs.getString("customerName"));

            // Add to observable list
            allCustomers.add(currentCustomer);
        }

        // Set Promp Text
        customerComboBox.setPromptText("Please select customer");
        typeComboBox.setPromptText("Please select type");
        consultantComboBox.setPromptText("Please select consultant");

        // Set list options
        customerComboBox.setItems(allCustomers);
        typeComboBox.setItems(allTypes);
        consultantComboBox.setItems(allConsultants);

        // Set start/end time and date functions
        dateComboBox.setVisibleRowCount(8);

        // Set business hours to be from 6am to 5pm in user's local time.

//        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
//        LocalDateTime localDateTime = utc.toLocalDateTime();
//        System.out.println("Local date time of UTC: " + localDateTime);
//
//        int startHour = localDateTime.getHour();
//        int endHour = localDateTime.plusHours(9).getHour();

        LocalTime start = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(17, 0);




        LocalDate date = LocalDate.now();
        LocalDate furthestAllowableDate = date.plusMonths(3);

        while(start.isBefore(end.plusSeconds(1))) {
            startComboBox.getItems().add(start);
            start = start.plusMinutes(10);
        }
        start = LocalTime.of(6, 0);

        while(end.isAfter(start.plusSeconds(1))) {
            endComboBox.getItems().add(end);
            end = end.minusMinutes(10);
        }

        while(date.isBefore(furthestAllowableDate)) {
            dateComboBox.getItems().add(date);
            date = date.plusDays(1);
        }

        startComboBox.getSelectionModel().select(LocalTime.of(8, 0));
        endComboBox.getSelectionModel().select(LocalTime.of(8, 10));
        dateComboBox.getSelectionModel().select(LocalDate.now());
    }

    public void loadSelectedAppointment(Appointment selectedAppointment) throws SQLException{
        //load selected appointment
        startComboBox.getSelectionModel().select(selectedAppointment.getLocalStart());
        endComboBox.getSelectionModel().select(selectedAppointment.getLocalEnd());
        dateComboBox.getSelectionModel().select(selectedAppointment.getLocalDate());
        isModifyScreen = true;
        apptTitle.setText("Modify Appointment");
        selectedAppointmentId = selectedAppointment.getAppointmentId();


        // Get Customer Id
        int selectedCustomerId = selectedAppointment.getCustomerId();
        PreparedStatement statement = DBQuery.getPreparedStatement();

        String selectCustomerStatement = "SELECT * FROM customer WHERE customerId =" + selectedCustomerId;

        statement.execute(selectCustomerStatement);
        ResultSet rs = statement.getResultSet();

        Customer selectedCustomer = new Customer();
        while(rs.next()) {
            selectedCustomer.setCustomerName(rs.getString("customerName"));
            selectedCustomer.setCustomerId(rs.getInt("customerId"));
            customerComboBox.getSelectionModel().select(selectedCustomer);
        }

        consultantComboBox.getSelectionModel().select(selectedAppointment.getContact());
        typeComboBox.getSelectionModel().select(selectedAppointment.getType());
        title.setText(selectedAppointment.getTitle());
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

    public void onSave(ActionEvent event) throws SQLException, IOException {
        LocalDate currentDate = dateComboBox.getSelectionModel().getSelectedItem();
        LocalTime currentStartTime = startComboBox.getSelectionModel().getSelectedItem();
        LocalTime currentEndTime = endComboBox.getSelectionModel().getSelectedItem();
        Customer currentCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        String currentConsultant = consultantComboBox.getSelectionModel().getSelectedItem();
        String currentType = typeComboBox.getSelectionModel().getSelectedItem();
        String currentTitle = title.getText();

        // Verify no invalid fields

        if (currentDate == null) {
            error = "You must select a date for the appointment";
        } else if (currentCustomer == null) {
            error = "You must select a customer for the appointment";
        } else if (currentConsultant == null) {
            error = "You must select a consultant for the appointment";
        } else if (currentType == null) {
            error = "You must select an appointment type";
        } else if (currentTitle == null || currentTitle.length() < 1) {
            error = "You must set a title for the appointment";
        } else if (currentStartTime.equals(currentEndTime)) {
            error = "The appointment start and end times cannot be the same";
        } else if (currentEndTime.isBefore(currentStartTime)) {
            error = "The appointment end time cannot be before the start time";
        }

        if (error.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Records Error");
            alert.setContentText(error);
            alert.showAndWait();

            error = "";
            return;
        }
        // Validate appt time here

        LocalDateTime currentStartDatetime = LocalDateTime.of(currentDate, currentStartTime);
        LocalDateTime currentEndDateTime = LocalDateTime.of(currentDate, currentEndTime);
        ZoneId localZone = ZoneId.systemDefault();

        ZonedDateTime zonedStartTime = currentStartDatetime.atZone(localZone).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime zonedEndTime = currentEndDateTime.atZone(localZone).withZoneSameInstant(ZoneId.of("UTC"));

        Timestamp databaseStartTime = Timestamp.valueOf(zonedStartTime.toLocalDateTime());
        Timestamp databaseEndTime = Timestamp.valueOf(zonedEndTime.toLocalDateTime());

        // Generate Timestamp for now
        Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
        Instant instant = nowTimestamp.toInstant();
        Timestamp timestampForDatabase = Timestamp.from(instant);

        // Check for schedule conflict
        boolean isConflict = checkScheduleConflict(databaseStartTime, databaseEndTime);
        if (isConflict) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Records Update");
            alert.setContentText("Appointment could not be saved due to schedule conflict.  Please try a different day or time.");
            alert.showAndWait();

            return;
        }


        Connection conn = DBConnection.startConnection();
        String addNewAppt = "INSERT INTO appointment (customerId, userId, title, contact, type, start, end, createDate, createdBy, lastUpdate, lastUpdateBy, description, location, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String updateAppt = "UPDATE appointment SET customerId = ?, userId = ?, title = ?, contact = ?, type = ?, start = ?, end = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ?, description = ?, location = ?, url = ? WHERE appointmentId = ?;";

        if (!isModifyScreen) {
            DBQuery.setPreparedStatement(conn, addNewAppt);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            // Add new country
            ps.setInt(1, currentCustomer.getCustomerId());
            ps.setInt(2, User.getUserId());
            ps.setString(3, currentTitle);
            ps.setString(4, currentConsultant);
            ps.setString(5, currentType);
            ps.setString(6, databaseStartTime.toString());
            ps.setString(7, databaseEndTime.toString());
            ps.setTimestamp(8, timestampForDatabase);
            ps.setString(9, User.getUsername());
            ps.setTimestamp(10, timestampForDatabase);
            ps.setString(11, User.getUsername());
            ps.setString(12, "N/A");
            ps.setString(13, "Virtual");
            ps.setString(14, "www.meet.com");


            ps.execute();
        } else {
            DBQuery.setPreparedStatement(conn, updateAppt);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ps.setInt(1, currentCustomer.getCustomerId());
            ps.setInt(2, User.getUserId());
            ps.setString(3, currentTitle);
            ps.setString(4, currentConsultant);
            ps.setString(5, currentType);
            ps.setString(6, databaseStartTime.toString());
            ps.setString(7, databaseEndTime.toString());
            ps.setTimestamp(8, timestampForDatabase);
            ps.setString(9, User.getUsername());
            ps.setTimestamp(10, timestampForDatabase);
            ps.setString(11, User.getUsername());
            ps.setString(12, "N/A");
            ps.setString(13, "Virtual");
            ps.setString(14, "www.meet.com");
            ps.setInt(15, selectedAppointmentId);


            ps.execute();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Records Update");
        alert.setContentText("Appointment was saved successfully");
        alert.showAndWait();

        onCancel(event);

    }

    private boolean checkScheduleConflict(Timestamp start, Timestamp end) throws SQLException {
        String consultant = consultantComboBox.getSelectionModel().getSelectedItem();

        String getConflictStatement = "SELECT * FROM appointment WHERE ? BETWEEN start AND end OR ? BETWEEN start and end OR ? < start AND ? > end OR ? = start OR ? = end AND contact = ?;";

        Connection conn = DBConnection.startConnection();
        DBQuery.setPreparedStatement(conn, getConflictStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();


        String startString = start.toString();
        String endString = end.toString();

        ps.setString(1, startString);
        ps.setString(2, endString);
        ps.setString(3, startString);
        ps.setString(4, endString);
        ps.setString(5, startString);
        ps.setString(6, endString);
        ps.setString(7, consultant);

        ps.execute();
        ResultSet results = ps.executeQuery();
        if (results.next()) {
            return true;
        } else {
            return false;
        }
    }
}
