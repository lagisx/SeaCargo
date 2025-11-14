package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import com.example.seacargo.controllers.CardController.GruzCardController;
import com.example.seacargo.controllers.CardController.FlightCardController;
import com.example.seacargo.models.Cargo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.*;

public class LogisticPanelController {
    public String user;
    public String password;

    @FXML private Label LabelUser;
    @FXML private VBox allCargoBox, allFlightsBox, freeFlightsBox;
    @FXML private javafx.scene.control.ScrollPane allCargoPane, allFlightsPane, freeFlightsPane;

    public static void LogistPanel(Stage stage, String user, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LogisticPanel.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LogisticPanelController controller = fxmlLoader.getController();
        controller.setNameUser(user);
        controller.setPassUsername(user, password);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    public void setNameUser(String user) {
        this.user = user;
        if (LabelUser != null) LabelUser.setText(user);
    }

    public void setPassUsername(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @FXML public void initialize() {
        showAllCargo();
    }

    private void hideAll() {
        allCargoPane.setVisible(false);
        allFlightsPane.setVisible(false);
        freeFlightsPane.setVisible(false);
    }

    @FXML private void showAllCargo() {
        hideAll();
        allCargoPane.setVisible(true);
        allCargoBox.getChildren().clear();

        String query = "SELECT * FROM cargo";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seacargo/Card/GruzCard.fxml"));
                AnchorPane card = loader.load();
                GruzCardController controller = loader.getController();

                Cargo cargo = new Cargo(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("weight"),
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getInt("user_id"),
                        rs.getString("status")
                );

                controller.setData(
                        cargo.getName(),
                        cargo.getWeight(),
                        cargo.getSender(),
                        cargo.getReceiver(),
                        "/images/cargo.png",
                        false,
                        cargo.getStatus()
                );

                allCargoBox.getChildren().add(card);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void showAllFlights() {
        hideAll();
        allFlightsPane.setVisible(true);
        allFlightsBox.getChildren().clear();

        String query = "SELECT * FROM flights";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seacargo/Card/FlightsCard.fxml"));
                AnchorPane card = loader.load();
                FlightCardController controller = loader.getController();

                controller.setData(
                        rs.getInt("id"),
                        rs.getString("flight_number"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getDate("flight_date").toString(),
                        rs.getString("status")
                );

                allFlightsBox.getChildren().add(card);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showFreeFlights() {
        hideAll();
        freeFlightsPane.setVisible(true);
        freeFlightsBox.getChildren().clear();

        String query = "SELECT * FROM flights WHERE id NOT IN (SELECT flight_id FROM cargo WHERE flight_id IS NOT NULL)";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seacargo/Card/FlightsCard.fxml"));
                AnchorPane card = loader.load();
                FlightCardController controller = loader.getController();

                controller.setData(
                        rs.getInt("id"),
                        rs.getString("flight_number"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getDate("flight_date").toString(),
                        rs.getString("status")
                );

                freeFlightsBox.getChildren().add(card);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void openAddFlightWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seacargo/Add/AddFlight.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Создать рейс");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
