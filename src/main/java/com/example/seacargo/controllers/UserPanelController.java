package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import com.example.seacargo.controllers.CardController.GruzCardController;
import com.example.seacargo.models.Cargo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.io.IOException;

public class UserPanelController {
    public String user;
    public String password;
    @FXML private ScrollPane myCargoPane;
    @FXML private ScrollPane allCargoPane;

    @FXML private VBox myCargoBox;
    @FXML private VBox allCargoBox;

    public void initialize() {
        loadCargos();
        getUserIdByLogin(user);
    }

    @FXML private Label LabelUser;

    public String getUser() {
        return user;
    }
    public void setNameUser(String user) {
        this.user = user;
        if (LabelUser != null) {
            LabelUser.setText(user);
        }
        loadMyCargos();
    }
    public void setPassUsername(String user, String password) {
        this.user = user;
        this.password = password;
    }
    public static void UserPanel(Stage stage, String user, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("UserPanel.fxml"));
        Scene userScene = new Scene(fxmlLoader.load());

        UserPanelController controller = fxmlLoader.getController();
        controller.setNameUser(user);
        controller.setPassUsername(user, password);

        stage.setTitle("Панель пользователя");
        stage.setScene(userScene);
        stage.show();
        stage.centerOnScreen();
    }
    @FXML private void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) LabelUser.getScene().getWindow();
        stage.setScene(loginScene);
        stage.setTitle("Авторизация");
        stage.centerOnScreen();
    }

    @FXML private void showMyCargo() {
        myCargoPane.setVisible(true);
        allCargoPane.setVisible(false);
    }
    @FXML public void showAllCargo() {
        myCargoPane.setVisible(false);
        allCargoPane.setVisible(true);
    }
    public void loadCargos() {
        int userId = getUserIdByLogin(getUser());
        allCargoBox.getChildren().clear();
        myCargoBox.getChildren().clear();

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

                boolean isOwner = cargo.getUserId() == userId;

                controller.setData(
                        cargo.getName(),
                        cargo.getWeight(),
                        cargo.getSender(),
                        cargo.getReceiver(),
                        "/images/cargo.png",
                        isOwner,
                        cargo.getStatus()
                );

                if (isOwner) myCargoBox.getChildren().add(card);
                allCargoBox.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadMyCargos() {
        int userId = getUserIdByLogin(getUser());
        myCargoBox.getChildren().clear();

        String query = "SELECT * FROM cargo WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(
                LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int cargoId = rs.getInt("id");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seacargo/Card/GruzCard.fxml"));
                    AnchorPane card = loader.load();
                    GruzCardController controller = loader.getController();

                    Cargo cargo = new Cargo(
                            cargoId,
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
                            true,
                            cargo.getStatus()
                    );
                        controller.deleteBtn.setOnMouseClicked(event -> {
                            deleteMyGruz(cargoId);
                            myCargoBox.getChildren().remove(card);
                        });

                    myCargoBox.getChildren().add(card);
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private void deleteMyGruz(int gruzId) {
        String delFromFlightCargo = "DELETE FROM flight_cargo WHERE cargo_id = ?";
        String delUserCargo = "DELETE FROM cargo WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD)) {
            try (PreparedStatement ps1 = conn.prepareStatement(delFromFlightCargo)) {
                ps1.setInt(1, gruzId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(delUserCargo)) {
                ps2.setInt(1, gruzId);
                ps2.executeUpdate();
            }

            loadCargos();
            loadMyCargos();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int getUserIdByLogin(String login) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection con = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @FXML private void openAddCargoWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seacargo/Card/AddCargoCard.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));

        AddCargoController controller = loader.getController();
        controller.setUsername(user);
        controller.setParentController(this);

        stage.setTitle("Добавить новый груз");
        stage.showAndWait();
    }


    public void setUser(String user) {
        this.user=user;
    }
}
