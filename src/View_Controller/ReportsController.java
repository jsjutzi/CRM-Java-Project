package View_Controller;

import Model.Appointment;
import Model.Customer;
import Model.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.DBQuery;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class ReportsController {
    private ObservableList<Report> appointmentsByMonth = FXCollections.observableArrayList();
    private ObservableList<Appointment> scheduleByConsultant = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentsByCustomer = FXCollections.observableArrayList();

    @FXML
    TableView appointmentsByMonthTableView;

    @FXML
    private TableColumn<Appointment, String> monthNameCol;

    @FXML
    private TableColumn<Appointment, Integer> totalApptsCol;

    @FXML
    TableView scheduleByConsultantTableView;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Appointment, String> dateCol;

    @FXML
    private TableColumn<Appointment, Integer> startCol;

    @FXML
    private TableColumn<Appointment, Integer> endCol;

    @FXML
    TableView appointmentsByCustomerTableView;

    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;

    @FXML
    private TableColumn<Appointment, String> customerNameCol;

    @FXML
    private TableColumn<Appointment, Integer> numOfApptsCol;



    public void initialize() throws SQLException {
        String getAppointmentsByCustomer = "SELECT DISTINCT c.customerId, c.customerName, (SELECT COUNT(*) FROM appointment a WHERE a.customerId = c.customerId) as numOfAppointments FROM customer c;";
        String getAppointmentsByMonth = "SELECT MONTHNAME(start) AS MONTH, (SELECT COUNT(DISTINCT type) from appointment b WHERE MONTHNAME(b.start) = MONTH) as TOTAL FROM appointment a GROUP BY MONTH;";
        String getScheduleByConsultant= "SELECT contact, start, end FROM appointment ORDER BY contact, start;";

        // Make db calls and populate tables

        PreparedStatement statement = DBQuery.getPreparedStatement();
        statement.execute(getAppointmentsByCustomer);
        ResultSet rs = statement.getResultSet();

        while (rs.next()) {
            Appointment currentAppointment = new Appointment();
            currentAppointment.setCustomerId(rs.getInt("customerId"));
            currentAppointment.setCustomerName(rs.getString("customerName"));
            currentAppointment.setNumOfAppointments(rs.getInt("numOfAppointments"));
            appointmentsByCustomer.add(currentAppointment);
        }

        statement = DBQuery.getPreparedStatement();
        statement.execute(getAppointmentsByMonth);
        rs = statement.getResultSet();

        while (rs.next()) {
            Report currentReport = new Report();
            currentReport.setMonth(rs.getString("MONTH"));
            currentReport.setNumberOfAppts(rs.getInt("TOTAL"));

            appointmentsByMonth.add(currentReport);
        }

        statement = DBQuery.getPreparedStatement();
        statement.execute(getScheduleByConsultant);
        rs = statement.getResultSet();

        while (rs.next()) {
            Appointment currentAppointment = new Appointment();
            currentAppointment.setContact(rs.getString("contact"));

            // Format Dates
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

            scheduleByConsultant.add(currentAppointment);
        }

        // Populate Report Tables

        appointmentsByCustomerTableView.setItems(appointmentsByCustomer);
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        numOfApptsCol.setCellValueFactory(new PropertyValueFactory<>("numOfAppointments"));

        appointmentsByMonthTableView.setItems(appointmentsByMonth);
        monthNameCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        totalApptsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfAppts"));

        scheduleByConsultantTableView.setItems(scheduleByConsultant);
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("localDate"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("localStart"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("localEnd"));
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
}
