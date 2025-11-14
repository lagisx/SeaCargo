package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgetPassController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label labelError;

    @FXML
    private ImageView forgotPasswordImage;

    private final String DB_URL = LoginController.DB_URL;
    private final String DB_USER = LoginController.DB_USER;
    private final String DB_PASSWORD = LoginController.DB_PASSWORD;

    public void initialize() {
        LoginController.loadLoginImage(forgotPasswordImage);
    }

    @FXML
    private void resetPassword() {
        String login = loginField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (login.isEmpty() || newPassword.isEmpty()) {
            labelError.setText("Заполните все поля");
            return;
        }
        if (newPassword.length() < 8 || newPassword.length() > 10) {
            labelError.setText("Пароль должен быть от 8 до 10 символов");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String checkQuery = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, login);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    labelError.setText("Пользователь не найден");
                    return;
                }
            }

            String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, login);
                int rows = updateStmt.executeUpdate();

                if (rows > 0) {
                    labelError.setStyle("-fx-text-fill: green;");
                    labelError.setText("Пароль успешно изменён");
                    backToLogins();
                } else {
                    labelError.setText("Ошибка при изменении пароля");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            labelError.setText("Ошибка подключения к базе данных");
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            FXMLLoader regfxml = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
            Scene scene1 = new Scene(regfxml.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.centerOnScreen();
            stage.setScene(scene1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToLogins() {
        try {
            FXMLLoader regfxml = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
            Scene scene1 = new Scene(regfxml.load());
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.centerOnScreen();
            stage.setScene(scene1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
