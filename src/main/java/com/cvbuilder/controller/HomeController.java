package com.cvbuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Button createCVButton;
    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        welcomeLabel.setText("Welcome to CV Builder!");
    }

    @FXML
    private void handleCreateCV() {
        // Logic to navigate to the CV form
    }
}
