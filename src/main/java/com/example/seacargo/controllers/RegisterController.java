package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class RegisterController {
    @FXML private RadioButton driverRadio;
    @FXML private RadioButton logistRadio;
    private ToggleGroup roleGroup;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private ImageView regImageView;

    public void initialize() {
        LoginController.loadLoginImage(regImageView);
        roleGroup = new ToggleGroup();
        driverRadio.setToggleGroup(roleGroup);
        logistRadio.setToggleGroup(roleGroup);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Пароли не совпадают");
            return;
        }
        if (selectedRole == null) {
            errorLabel.setText("Выберите роль");
            return;
        }

        String role = selectedRole.getText();

        if (password.length() < 8 || password.length() > 15) {
            errorLabel.setText("Пароль должен быть от 8 до 15 символов");
            return;
        }

        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL,
                LoginController.DB_USER, LoginController.DB_PASSWORD)) {

            PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                errorLabel.setText("Email уже используется");
                return;
            }


            PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users(username, email, password, role) VALUES (?, ?, ?, ?)");
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.setString(4, role);
            insertStmt.executeUpdate();

            System.out.println("Регистрация успешна: " + username + ", " + email + ", " + role);

            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();

        } catch (SQLException e) {
            errorLabel.setText("Ошибка при регистрации: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void goBackOnLogin(ActionEvent event) {
        try { FXMLLoader regfxml = new FXMLLoader(HelloApplication.class.getResource("LoginPanel.fxml"));
            Scene scene1 = new Scene(regfxml.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.centerOnScreen(); stage.setScene(scene1); }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
