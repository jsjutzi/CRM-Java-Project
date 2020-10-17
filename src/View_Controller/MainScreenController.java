package View_Controller;

import Model.Appointment;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.DBQuery;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class MainScreenController {
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private boolean isAllView;
    private boolean isMonthView;
    private boolean isWeekView;

    // Time and Date
    private DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter dateDTF = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private DateTimeFormatter timeDTF = DateTimeFormatter.ofPattern("HH:mm");


    private ZoneId localZoneID = ZoneId.systemDefault();
    private ZoneId utcZoneID = ZoneId.of("UTC");

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableView<Appointment> appointmentsTableView;

    @FXML
    private TableColumn<Customer, Integer> customerIdCol;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, String> customerAddressCol;

    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    @FXML
    private TableColumn<Appointment, String> customerNameApptCol;

    @FXML
    private TableColumn<Appointment, String> dateCol;

    @FXML
    private TableColumn<Appointment, String> startCol;

    @FXML
    private TableColumn<Appointment, String> endCol;

    @FXML
    ToggleGroup toggleDateRange;

    @FXML
    RadioButton allView;

    @FXML
    RadioButton monthView;

    @FXML
    RadioButton weekView;

    public void noop() {

    }

    public void initialize() throws SQLException {
        allCustomers.clear();
        allAppointments.clear();

        PreparedStatement statement = DBQuery.getPreparedStatement();

        String selectCustomersStatement = "SELECT customerId, customerName, address.addressId, address, address2, city.cityId, city, postalCode, country.countryId, country, phone,  active FROM customer, address, city, country WHERE customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId ORDER BY customer.customerName;";

        statement.execute(selectCustomersStatement);
        ResultSet rs = statement.getResultSet();
        while (rs.next()) {
            Customer currentCustomer = new Customer();
            currentCustomer.setCustomerId(rs.getInt("customerId"));
            currentCustomer.setCustomerName(rs.getString("customerName"));
            currentCustomer.setAddress(rs.getString("address"));
            currentCustomer.setAddress2(rs.getString("address2"));
            currentCustomer.setCity(rs.getString("city"));
            currentCustomer.setPostalCode(rs.getString("postalCode"));
            currentCustomer.setCountry(rs.getString("country"));
            currentCustomer.setActive(rs.getInt("active"));
            currentCustomer.setPhone(rs.getString("phone"));
            currentCustomer.setAddressId(rs.getInt("addressId"));
            currentCustomer.setCityId(rs.getInt("cityId"));
            currentCustomer.setCountryId(rs.getInt("countryId"));



            // Add to observable list
            allCustomers.add(currentCustomer);
        }

        // Populate Customer Table
        customerTableView.setItems(allCustomers);
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Get Appointment Data and Populate Appointment table
        String selectAppointmentsStatement = "SELECT  a.appointmentId, a.customerId, c.customerName, a.userId, a.title, a.description, a.location, a.contact, a.type, a.url, a.start, a.end, a.createDate, a.createdBy, a.lastUpdate, a.lastUpdateBy FROM appointment a INNER JOIN customer c ON a.customerId = c.customerId ORDER BY start;";
        statement.execute(selectAppointmentsStatement);

        rs = statement.getResultSet();
        while (rs.next()) {
            Appointment currentAppointment = new Appointment();
            currentAppointment.setAppointmentId(rs.getInt("appointmentId"));
            currentAppointment.setCustomerId(rs.getInt("customerId"));
            currentAppointment.setCustomerName(rs.getString("customerName"));
            currentAppointment.setTitle(rs.getString("title"));
            currentAppointment.setDescription(rs.getString("description"));
            currentAppointment.setLocation(rs.getString("location"));
            currentAppointment.setContact(rs.getString("contact"));
            currentAppointment.setType(rs.getString("type"));
            currentAppointment.setUrl(rs.getString("url"));
            currentAppointment.setCreateDate(rs.getTimestamp("createDate"));
            currentAppointment.setCreatedBy(rs.getString("createdBy"));
            currentAppointment.setLastUpdate(rs.getTimestamp("lastUpdate"));
            currentAppointment.setLastUpdateBy(rs.getString("lastUpdateBy"));

            // convert database times to local
            ZoneId localZone = ZoneId.systemDefault();
            Timestamp startUTC = rs.getTimestamp("start");
            Timestamp endUTC = rs.getTimestamp("end");

            LocalDateTime utcStartDT = startUTC.toInstant().atZone(localZone).toLocalDateTime();
            LocalDateTime utcEndDT = endUTC.toInstant().atZone(localZone).toLocalDateTime();

            //convert database UTC to LocalDateTime

            // Retain local date and time formats
            LocalDate localDate = utcStartDT.toLocalDate();
            LocalTime localStart = utcStartDT.toLocalTime();
            LocalTime localEnd = utcEndDT.toLocalTime();

            currentAppointment.setLocalDate(localDate);
            currentAppointment.setLocalStart(localStart);
            currentAppointment.setLocalEnd(localEnd);

            // Split appt times/date
            String apptDate = localDate.format(dateDTF);
            String apptStartTime = localStart.format(timeDTF);
            String apptEndTime = localEnd.format(timeDTF);

            currentAppointment.setDate(apptDate);
            currentAppointment.setStart(apptStartTime);
            currentAppointment.setEnd(apptEndTime);

            //Filter appointments by month
            if (isMonthView) {
                LocalDate today = LocalDate.now();
                LocalDate todayPlusMonth = today.plusMonths(1);

                LocalDate currentApptDate = localDate;
                if (currentApptDate.isAfter(today.minusDays(1)) && currentApptDate.isBefore(todayPlusMonth)) {
                    allAppointments.add(currentAppointment);
                }

            } else if (isWeekView) {
                LocalDate today = LocalDate.now();
                LocalDate todayPlusWeek = today.plusWeeks(1);

                LocalDate currentApptDate = localDate;
                if (currentApptDate.isAfter(today.minusDays(1)) && currentApptDate.isBefore(todayPlusWeek)) {
                    allAppointments.add(currentAppointment);
                }
            } else {
                allAppointments.add(currentAppointment);
            }
        }

        appointmentsTableView.setItems(allAppointments);
        customerNameApptCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));


    }
   // TODO: Condense to one function - pull id off ActionEvent
    public void selectAllView() throws SQLException {
        isAllView = true;
        isMonthView = false;
        isWeekView = false;

        initialize();
    }

    public void selectMonthView() throws SQLException {
        isAllView = false;
        isMonthView = true;
        isWeekView = false;

        initialize();
    }
    public void selectWeekView() throws SQLException {
        isAllView = false;
        isMonthView = false;
        isWeekView = true;

        initialize();
    }

    public void deleteAppt() throws SQLException {
        Appointment selectedAppt = appointmentsTableView.getSelectionModel().getSelectedItem();
        int selectedApptId = selectedAppt.getAppointmentId();

        Statement statement = DBQuery.getPreparedStatement();

        String deleteStatement = "DELETE FROM appointment WHERE appointmentId = " + selectedApptId;
        statement.execute(deleteStatement);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Records Update");
        alert.setContentText("Appointment was deleted successfully");
        alert.showAndWait();

        initialize();

    }

    public void addModifyCustomer(ActionEvent event) throws IOException  {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("add_modify_customer.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        AddModifyCustomerController controller = loader.getController();
        String buttonClicked = ((Button)event.getSource()).getText();

        // Load selected customer if modify button was clicked
        if (buttonClicked.equals("Modify")) {
            Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
            controller.loadSelectedCustomer(selectedCustomer);
        }

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void addModifyAppt(ActionEvent event) throws IOException, SQLException  {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("add_modify_appt.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        AddModifyApptController controller = loader.getController();
        String buttonClicked = ((Button)event.getSource()).getText();

        // Load selected customer if modify button was clicked
        if (buttonClicked.equals("Modify")) {
            Appointment selectedAppt = appointmentsTableView.getSelectionModel().getSelectedItem();
            controller.loadSelectedAppointment(selectedAppt);
        }

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }



    public void deleteCustomer() throws SQLException {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        int selectedCustomerId = selectedCustomer.getCustomerId();

        Statement statement = DBQuery.getPreparedStatement();

        String deleteStatement = "DELETE FROM customer WHERE customerId = " + selectedCustomerId;
        statement.execute(deleteStatement);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Records Update");
        alert.setContentText("Customer Record was deleted successfully");
        alert.showAndWait();

        initialize();
    }

    public void exitProgram() {
        System.exit(0);
    }
}
