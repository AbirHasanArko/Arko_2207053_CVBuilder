package com.cvbuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class FormController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Button submitButton;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void handleSubmit() {
        // Validation and processing logic
    }
}
