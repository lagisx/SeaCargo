package com.example.seacargo.controllers;

import com.example.seacargo.models.Cargo;
import com.example.seacargo.models.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.*;
import java.time.LocalDate;

public class AssignCargoController {

    @FXML
    private ListView<Cargo> cargoListView;

    @FXML
    private ListView<Flight> flightListView;

    private ObservableList<Cargo> cargoList = FXCollections.observableArrayList();
    private ObservableList<Flight> flightList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadCargos();
        loadFlights();
        cargoListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        flightListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
    }

    private void loadCargos() {
        cargoList.clear();
        String query = "SELECT * FROM cargo c WHERE NOT EXISTS (SELECT 1 FROM flight_cargo fc WHERE fc.cargo_id = c.id)";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cargoList.add(new Cargo(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("weight"),
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getInt("user_id"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        cargoListView.setItems(cargoList);
    }

    public void loadFlights() {
        flightList.clear();
        String query = "SELECT * FROM flights WHERE status != 'Занят'";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String flightNum = rs.getString("flight_number");
                String departure = rs.getString("departure");
                String destination = rs.getString("destination");
                LocalDate flightDate = rs.getDate("flight_date").toLocalDate();
                String status =  rs.getString("status");

                flightList.add(new Flight(id, flightNum, departure, destination, flightDate, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        flightListView.setItems(flightList);
    }

    private boolean canAssignCargo(int flightId) {
        String query = "SELECT COUNT(*) FROM flight_cargo WHERE flight_id = ?";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) < 3; // максимум 3 груза
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void assignCargoToFlight() {
        Flight selectedFlight = flightListView.getSelectionModel().getSelectedItem();
        Cargo selectedCargo = cargoListView.getSelectionModel().getSelectedItem();
        if (selectedFlight == null || selectedCargo == null) return;

        if (canAssignCargo(selectedFlight.getId())) {
            String insert = "INSERT INTO flight_cargo (flight_id, cargo_id) VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(insert)) {
                stmt.setInt(1, selectedFlight.getId());
                stmt.setInt(2, selectedCargo.getId());
                stmt.executeUpdate();

                updateFlightStatus(selectedFlight.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Рейс " + selectedFlight.getFlightNumber() + " уже заполнен (макс. 3 груза).");
        }

        loadCargos();
        loadFlights();
    }

    private void updateFlightStatus(int flightId) {
        String countQuery = "SELECT COUNT(*) FROM flight_cargo WHERE flight_id = ?";
        String updateQuery = "UPDATE flights SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement countStmt = conn.prepareStatement(countQuery)) {

            countStmt.setInt(1, flightId);
            ResultSet rs = countStmt.executeQuery();
            if (rs.next()) {
                int cargoCount = rs.getInt(1);
                String newStatus = cargoCount >= 3 ? "Занят" : "Планируется";

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newStatus);
                    updateStmt.setInt(2, flightId);
                    updateStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
