package com.example.seacargo.controllers.CardController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GruzCardController {

    @FXML public ImageView cargoImage;
    @FXML public Label cargoName;
    @FXML public Label cargoWeight;
    @FXML public Label cargoSender;
    @FXML public Label cargoReceiver;
    @FXML public Button deleteBtn;

    public void setData(String name, String weight, String sender, String receiver, String imagePath, boolean isOwner, String status) {
        cargoName.setText(name);
        cargoWeight.setText(weight);
        cargoSender.setText("Отправитель: " + sender);
        cargoReceiver.setText("Получатель: " + receiver);
        if (imagePath != null && !imagePath.isEmpty()) {
            cargoImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        }

        deleteBtn.setVisible(isOwner);
    }
}
