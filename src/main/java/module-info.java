module com.example.seacargo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    opens com.example.seacargo to javafx.fxml;
    exports com.example.seacargo;
    exports com.example.seacargo.controllers;
    opens com.example.seacargo.controllers to javafx.fxml;
    opens com.example.seacargo.controllers.CardController to javafx.fxml;
}