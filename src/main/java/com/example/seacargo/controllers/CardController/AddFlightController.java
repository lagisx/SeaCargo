package com.example.seacargo.controllers.CardController;

import com.example.seacargo.controllers.AssignCargoController;
import com.example.seacargo.controllers.LoginController;
import com.example.seacargo.controllers.LogisticPanelController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.*;

public class AddFlightController {

    @FXML private TextField flightNumberField;
    @FXML private TextField departureField;
    @FXML private TextField destinationField;
    @FXML private DatePicker flightDatePicker;

    private LogisticPanelController parentController;

    public void setParentController(LogisticPanelController controller) {
        this.parentController = controller;
    }

    @FXML private void addFlight() {
        String flightNumber = flightNumberField.getText().trim();
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        java.time.LocalDate flightDate = flightDatePicker.getValue();

        if (flightNumber.isEmpty() || departure.isEmpty() || destination.isEmpty() || flightDate == null) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля.");
            return;
        }

        String query = "INSERT INTO flights (flight_number, departure, destination, flight_date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, flightNumber);
            stmt.setString(2, departure);
            stmt.setString(3, destination);
            stmt.setDate(4, Date.valueOf(flightDate));
            stmt.setString(5, "Планируется");
            stmt.executeUpdate();

            if (parentController != null) {
                parentController.showAllFlights();
                parentController.showFreeFlights();
            }
            showAlert("Успех", "Рейс успешно добавлен!");
            closeWindow();


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось добавить рейс.");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) flightNumberField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
