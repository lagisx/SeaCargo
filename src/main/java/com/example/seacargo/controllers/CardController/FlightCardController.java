package com.example.seacargo.controllers.CardController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FlightCardController {

    @FXML private Label flightName;
    @FXML private Label flightInfo;
    @FXML private Button assignCargoBtn;

    private int flightId;

    public void setData(int id, String flightNumber, String departure, String destination, String date, String status) {
        this.flightId = id;
        flightName.setText(flightNumber + " (" + status + ")");
        flightInfo.setText(departure + " → " + destination + ", Дата: " + date);

        assignCargoBtn.setOnAction(e -> openAssignCargoWindow());
    }

    private void openAssignCargoWindow() {
        System.out.println("Открыть назначение грузов для рейса ID: " + flightId);
        // TODO: окно выбора грузов для назначения
    }
}
