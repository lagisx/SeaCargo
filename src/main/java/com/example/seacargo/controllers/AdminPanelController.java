package com.example.seacargo.controllers;

import com.example.seacargo.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPanelController {
    public static void AdminPanel(Stage stage, String user, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AdminPanel.fxml"));
        Scene userScene = new Scene(fxmlLoader.load());
        AdminPanelController controller = fxmlLoader.getController();
        stage.setScene(userScene);
        stage.show();
        stage.centerOnScreen();
    }
}
