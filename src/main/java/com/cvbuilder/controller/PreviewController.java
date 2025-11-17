package com.cvbuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PreviewController {

    @FXML
    private Label previewLabel;

    public void setCVPreview(String preview) {
        previewLabel.setText(preview);
    }
}
