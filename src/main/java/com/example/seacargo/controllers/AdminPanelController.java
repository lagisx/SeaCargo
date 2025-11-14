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

    @FXML private void deleteUser() {
        Users selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;

        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selectedUser.getId());
            stmt.executeUpdate();
            loadUsers();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML private void editUser() {
        Users selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;

        TextInputDialog dialogLogin = new TextInputDialog(selectedUser.getUsername());
        dialogLogin.setTitle("Редактирование пользователя");
        dialogLogin.setHeaderText("Изменить логин пользователя");
        dialogLogin.setContentText("Логин:");

        dialogLogin.showAndWait().ifPresent(newLogin -> {
            TextInputDialog dialogPassword = new TextInputDialog(selectedUser.getPassword());
            dialogPassword.setTitle("Редактирование пользователя");
            dialogPassword.setHeaderText("Изменить пароль пользователя");
            dialogPassword.setContentText("Пароль:");

            dialogPassword.showAndWait().ifPresent(newPassword -> {
                TextInputDialog dialogEmail = new TextInputDialog(selectedUser.getEmail());
                dialogEmail.setTitle("Редактирование пользователя");
                dialogEmail.setHeaderText("Изменить Email пользователя");
                dialogEmail.setContentText("Email:");

                dialogEmail.showAndWait().ifPresent(newEmail -> {
                    TextInputDialog dialogRole = new TextInputDialog(selectedUser.getRole());
                    dialogRole.setTitle("Редактирование пользователя");
                    dialogRole.setHeaderText("Изменить роль пользователя");
                    dialogRole.setContentText("Роль:");

                    dialogRole.showAndWait().ifPresent(newRole -> {
                        updateUser(selectedUser.getId(), newLogin, newPassword, newEmail, newRole);
                    });
                });
            });
        });
    }

    private void updateUser(int userId, String username, String password, String email, String role) {
        String query = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setInt(5, userId);
            stmt.executeUpdate();

            loadUsers();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void editCargo() {
        Cargo selectedCargo = cargoTable.getSelectionModel().getSelectedItem();
        if (selectedCargo == null) return;

        TextInputDialog dialogName = new TextInputDialog(selectedCargo.getName());
        dialogName.setTitle("Редактирование груза");
        dialogName.setHeaderText("Изменить название груза");
        dialogName.setContentText("Название:");

        dialogName.showAndWait().ifPresent(newName -> {
            TextInputDialog dialogWeight = new TextInputDialog(selectedCargo.getWeight());
            dialogWeight.setTitle("Редактирование груза");
            dialogWeight.setHeaderText("Изменить вес груза");
            dialogWeight.setContentText("Вес:");

            dialogWeight.showAndWait().ifPresent(newWeight -> {
                TextInputDialog dialogSender = new TextInputDialog(selectedCargo.getSender());
                dialogSender.setTitle("Редактирование груза");
                dialogSender.setHeaderText("Изменить отправителя");
                dialogSender.setContentText("Отправитель:");

                dialogSender.showAndWait().ifPresent(newSender -> {
                    TextInputDialog dialogReceiver = new TextInputDialog(selectedCargo.getReceiver());
                    dialogReceiver.setTitle("Редактирование груза");
                    dialogReceiver.setHeaderText("Изменить получателя");
                    dialogReceiver.setContentText("Получатель:");

                    dialogReceiver.showAndWait().ifPresent(newReceiver -> {
                        updateCargo(selectedCargo.getId(), newName, newWeight, newSender, newReceiver);
                    });
                });
            });
        });
    }
    private void updateCargo(int cargoId, String name, String weight, String sender, String receiver) {
        String query = "UPDATE cargo SET name = ?, weight = ?, sender = ?, receiver = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, weight);
            stmt.setString(3, sender);
            stmt.setString(4, receiver);
            stmt.setInt(5, cargoId);
            stmt.executeUpdate();

            loadCargos();

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка при обновлении груза");
        }
    }

    @FXML private void deleteFlight() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) return;

        String query = "DELETE FROM flights WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selectedFlight.getId());
            stmt.executeUpdate();

            loadFlights();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML private void editFlight() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) return;

        TextInputDialog dialogNum = new TextInputDialog(selectedFlight.getFlightNumber());
        dialogNum.setTitle("Редактирование рейса");
        dialogNum.setHeaderText("Изменить номер рейса");
        dialogNum.setContentText("Номер:");

        dialogNum.showAndWait().ifPresent(newNum -> {
            TextInputDialog dialogDeparture = new TextInputDialog(selectedFlight.getDeparture());
            dialogDeparture.setTitle("Редактирование рейса");
            dialogDeparture.setHeaderText("Изменить отправление");
            dialogDeparture.setContentText("Отправление:");

            dialogDeparture.showAndWait().ifPresent(newDeparture -> {
                TextInputDialog dialogDest = new TextInputDialog(selectedFlight.getDestination());
                dialogDest.setTitle("Редактирование рейса");
                dialogDest.setHeaderText("Изменить пункт назначения");
                dialogDest.setContentText("Пункт назначения:");

                dialogDest.showAndWait().ifPresent(newDestination -> {
                    updateFlight(selectedFlight.getId(), newNum, newDeparture, newDestination);
                });
            });
        });
    }
    private void updateFlight(int flightId, String flightNumber, String departure, String destination) {
        String query = "UPDATE flights SET flight_number = ?, departure = ?, destination = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, flightNumber);
            stmt.setString(2, departure);
            stmt.setString(3, destination);
            stmt.setInt(4, flightId);
            stmt.executeUpdate();

            loadFlights();

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
