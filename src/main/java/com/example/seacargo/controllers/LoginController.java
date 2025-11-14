package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/SeaBD";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = " "; // ИЗМЕНИ ПАРОЛЬ НА СВОЙ, КОГДА ТЫ ЗАХОДИШЬ В POSTGRESQL;

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private ImageView loginImageView;

    public void initialize() {
        loadLoginImage(loginImageView);
    }

    public static void loadLoginImage(ImageView imageView) {
        Image image = new Image(LoginController.class.getResourceAsStream("/images/ship.png"));
        imageView.setImage(image);
    }

    @FXML
    private void connect(ActionEvent event) {
        String user = loginField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showStatus("Введите логин и пароль");
            return;
        }

        if (user.equalsIgnoreCase("postgres")) {
            if (pass.equals(DB_PASSWORD)) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                AdminPanelController adminPanel = new AdminPanelController();
                try {
                    adminPanel.AdminPanel(stage, user, pass);
                } catch (IOException e) {
                    showStatus("Ошибка при загрузке панели администратора");
                    e.printStackTrace();
                }
            } else {
                showStatus("Неверный логин или пароль");
            }
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, user);
                stmt.setString(2, pass);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        showPanel(user, stage, pass, role);
                    } else {
                        showStatus("Неверный логин или пароль");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showStatus("Ошибка подключения к базе данных");
        }
    }


    private void showStatus(String message) {
        errorLabel.setText(message);
    }


    private void showPanel(String user, Stage stage, String pass, String role) {
        try {
            if (user.equalsIgnoreCase("postgres")) {
                AdminPanelController AdminPanel = new AdminPanelController();
                AdminPanel.AdminPanel(stage, user, pass);
                return;
            }
            switch (role.toLowerCase()) {
                case "логист" -> {
                    LogisticPanelController LogistPanel = new LogisticPanelController();
                    LogistPanel.LogistPanel(stage, user, pass);
                }

                case "перевозчик" -> {
                    UserPanelController userPanel = new UserPanelController();
                    userPanel.UserPanel(stage, user, pass);
                }

                default -> {
                    showStatus("Неизвестная роль: " + role);
                    return;
                }
            }

        } catch (IOException e) {
            showStatus("Ошибка при загрузке панели: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ForgetPass.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Восстановление пароля");
            stage.centerOnScreen();
        } catch (IOException e) {
            showStatus("Ошибка при открытии восстановления пароля");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("RegPanel.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Регистрация");
            stage.centerOnScreen();
        } catch (IOException e) {
            showStatus("Ошибка при открытии регистрации");
            e.printStackTrace();
        }
    }
}
