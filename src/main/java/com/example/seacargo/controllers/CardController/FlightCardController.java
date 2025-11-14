package com.example.seacargo.controllers.CardController;

import com.example.seacargo.controllers.LoginController;
import com.example.seacargo.controllers.LogisticPanelController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FlightCardController {

    @FXML private Label flightName;
    @FXML private Label flightInfo;
    @FXML private Button assignCargoBtn;
    @FXML private Button deleteFlightBtn;
    private LogisticPanelController parentController;
    private String status;

    public void setParentController(LogisticPanelController controller) {
        this.parentController = controller;
    }


    private int flightId;

    public void setData(int id, String flightNumber, String departure, String destination, String date, String status) {
        this.flightId = id;
        this.status = status;
        flightName.setText(flightNumber + " (" + status + ")");
        flightInfo.setText(departure + " → " + destination + ", Дата: " + date);

        assignCargoBtn.setOnAction(e -> openAssignCargoWindow());
        deleteFlightBtn.setOnAction(e -> deleteFlight());


    }

    private void openAssignCargoWindow() {
        if ("Занят".equalsIgnoreCase(status)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Нельзя назначить груз на занятый рейс!");
            alert.showAndWait();
            return;
        }
        LogisticPanelController LogicPanel = new LogisticPanelController();
        LogicPanel.openAddFlightWindow();
    }

        private void deleteFlight() {
            try (Connection conn = DriverManager.getConnection(
                    LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD)) {

                String query = "DELETE FROM flights WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, flightId);
                stmt.executeUpdate();

                if (parentController != null) {
                    parentController.showAllFlights();
                    parentController.showFreeFlights();
                }

                Alert success = new Alert(Alert.AlertType.INFORMATION, "Рейс удален!");
                success.showAndWait();

            } catch (SQLException ex) {
                ex.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR, "Не удалось удалить рейс.");
                error.showAndWait();
            }
        }
    }

