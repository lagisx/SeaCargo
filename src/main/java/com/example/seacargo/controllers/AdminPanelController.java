package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import com.example.seacargo.models.Cargo;
import com.example.seacargo.models.Flight;
import com.example.seacargo.models.Users;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AdminPanelController {

    @FXML private Label LabelUser;

    @FXML private Button btnCargos, btnUsers, btnFlight;

    @FXML private ScrollPane cargoPane, usersPane, flightPane;

    @FXML private TableView<Cargo> cargoTable;
    @FXML private TableColumn<Cargo, Integer> colCargoId;
    @FXML private TableColumn<Cargo, String> colCargoOwner;
    @FXML private TableColumn<Cargo, String> colCargoName;
    @FXML private TableColumn<Cargo, String> colCargoWeight;
    @FXML private TableColumn<Cargo, String> colCargoSender;
    @FXML private TableColumn<Cargo, String> colCargoReceiver;
    @FXML private Button btnDeleteCargo;
    @FXML private Label statusLabel;

    @FXML private TableView<Users> usersTable;
    @FXML private TableColumn<Users, Integer> colUserId;
    @FXML private TableColumn<Users, String> colUserLogin;
    @FXML private TableColumn<Users, String> colUsersPassword;
    @FXML private TableColumn<Users, String> colUserEmail;
    @FXML private TableColumn<Users, String> colUserRole;
    @FXML private TableColumn<Users, String> colUserAt_created;

    @FXML private TableView<Flight> flightTable;
    @FXML private TableColumn<Flight, Integer> colFlightId;
    @FXML private TableColumn<Flight, String> colAuditUser;
    @FXML private TableColumn<Flight, String> colFlightNumber;
    @FXML private TableColumn<Flight, String> colFlightDeparture;
    @FXML private TableColumn<Flight, String> colFlightDestination;
    @FXML private TableColumn<Flight, String> colFlightDate;
    @FXML private TableColumn<Flight, String> colFlightStatus;

    private ObservableList<Cargo> cargoList = FXCollections.observableArrayList();
    private ObservableList<Users> userList = FXCollections.observableArrayList();
    private ObservableList<Flight> flightsList = FXCollections.observableArrayList();

    // Здесь укажи свои данные для подключения
    private final String DB_URL = LoginController.DB_URL;
    private final String DB_USER = LoginController.DB_USER;
    private final String DB_PASSWORD = LoginController.DB_PASSWORD;

    @FXML
    public void initialize() {
        loadColCargos();
        loadColUsers();
        loadColFlights();
        loadCargos();
        loadUsers();
        loadFlights();
        showCargos();
    }

    private void loadColCargos() {
        colCargoId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colCargoName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colCargoWeight.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getWeight()));
        colCargoSender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSender()));
        colCargoReceiver.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReceiver()));
        colCargoOwner.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getUserId()).asObject().asString());
    }

    private void loadColUsers() {
        colUserId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colUserLogin.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colUsersPassword.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPassword()));
        colUserEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colUserRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));
        colUserAt_created.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCreated_at()));
    }

    private void loadColFlights() {
        colFlightId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colFlightNumber.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFlightNumber()));
        colFlightDeparture.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDeparture()));
        colFlightDestination.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDestination()));
        colFlightDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDate().toString()));
        colAuditUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFlightNumber()));
        colFlightStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
    }

    @FXML
    private void showCargos() {
        cargoPane.setVisible(true);
        usersPane.setVisible(false);
        flightPane.setVisible(false);
    }

    @FXML
    private void showUsers() {
        cargoPane.setVisible(false);
        usersPane.setVisible(true);
        flightPane.setVisible(false);
    }

    @FXML
    private void showFlight() {
        cargoPane.setVisible(false);
        usersPane.setVisible(false);
        flightPane.setVisible(true);
    }

    @FXML
    private void deleteCargo() {
        Cargo selected = cargoTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = con.prepareStatement("DELETE FROM cargo WHERE id = ?")) {
                stmt.setInt(1, selected.getId());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    cargoTable.getItems().remove(selected);
                    statusLabel.setText("Груз удален успешно");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText("Ошибка при удалении груза");
            }
        } else {
            statusLabel.setText("Выберите груз для удаления");
        }
    }

    private void loadCargos() {
        cargoList.clear();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cargo")) {
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
            cargoTable.setItems(cargoList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        userList.clear();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                userList.add(new Users(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("created_at")
                ));
            }
            usersTable.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFlights() {
        flightsList.clear();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM flights")) {
            while (rs.next()) {
                flightsList.add(new Flight(
                        rs.getInt("id"),
                        rs.getString("flight_number"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getDate("flight_date").toLocalDate(),
                        rs.getString("status")
                ));
            }
            flightTable.setItems(flightsList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setNameUser(String user) {
        LabelUser.setText(user);
    }

    @FXML private void logout(ActionEvent event) {
        try { FXMLLoader regfxml = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
            Scene scene1 = new Scene(regfxml.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.centerOnScreen(); stage.setScene(scene1); }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void AdminPanel(Stage stage, String user, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminPanelController.class.getResource("/com/example/seacargo/AdminPanel.fxml"));
        Scene userScene = new Scene(fxmlLoader.load());
        AdminPanelController admincontroller = fxmlLoader.getController();
        admincontroller.setNameUser(user); stage.setTitle("Панель пользователя");
        stage.setScene(userScene); stage.show();
        stage.centerOnScreen();
    }
}
