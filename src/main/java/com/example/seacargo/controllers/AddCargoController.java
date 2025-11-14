package com.example.seacargo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCargoController {

    @FXML private TextField nameField;
    @FXML private TextField weightField;
    @FXML private TextField senderField;
    @FXML private TextField receiverField;
    @FXML private Button addCargoBtn;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    public void initialize() {
        addCargoBtn.setOnAction(event -> addNewCargo());
    }

    private void addNewCargo() {
        String name = nameField.getText();
        String weight = weightField.getText();
        String sender = senderField.getText();
        String receiver = receiverField.getText();

        if (name.isEmpty() || weight.isEmpty() || sender.isEmpty() || receiver.isEmpty()) {
            System.out.println("Заполните все поля");
            return;
        }

        int userId = UserPanelController.getUserIdByLogin(username);
        String insertQuery = "INSERT INTO cargo (name, weight, sender, receiver, user_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(LoginController.DB_URL, LoginController.DB_USER, LoginController.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setString(1, name);
            ps.setString(2, weight);
            ps.setString(3, sender);
            ps.setString(4, receiver);
            ps.setInt(5, userId);
            ps.setString(6, "Новый");
            ps.executeUpdate();

            if (parentController != null) {
                parentController.loadCargos();
                parentController.loadMyCargos();
            }

            Stage stage = (Stage) addCargoBtn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private UserPanelController parentController;
    public void setParentController(UserPanelController parentController) {
        this.parentController = parentController;
    }
}
