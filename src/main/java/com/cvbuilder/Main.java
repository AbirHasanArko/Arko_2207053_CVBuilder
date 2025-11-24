package com.cvbuilder;

import com.cvbuilder.database.CVData;
import com.cvbuilder.database.DatabaseManager;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private Stage primaryStage;
    private Scene homeScene;
    private Scene formScene;
    private Scene previewScene;
    private Scene savedCVsScene;
    private DatabaseManager dbManager;

    // Store current CV data for saving
    private String currentName, currentEmail, currentPhone, currentAddress;
    private String currentEducation, currentSkills, currentWork, currentProjects;
    private Integer currentCVId = null; // For editing existing CV

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CV Builder - Professional Resume Creator");
        this.dbManager = DatabaseManager.getInstance();

        showHomeScreen();
    }

    private void showHomeScreen() {
        currentCVId = null; // Reset when going to home

        // Create welcome title
        Label titleLabel = new Label("CV Builder");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Create welcome message
        Label welcomeLabel = new Label("Create Your Professional Curriculum Vitae");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #7f8c8d; -fx-padding: 10px;");

        // Create subtitle
        Label subtitleLabel = new Label("Build a stunning CV in minutes with this easy-to-use tool");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6; -fx-padding: 5px;");

        // Database stats
        int totalCVs = dbManager.getTotalCVCount();
        Label dbStatsLabel = new Label("📊 Total Saved CVs: " + totalCVs);
        dbStatsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-padding: 10px;");

        // Create "Create New CV" button
        Button createCVButton = new Button("📝 Create New CV");
        createCVButton.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-padding: 15px 40px; " +
                        "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-cursor: hand;"
        );

        // Add hover effect
        createCVButton.setOnMouseEntered(e ->
                createCVButton.setStyle(
                        "-fx-font-size: 18px; " +
                                "-fx-padding: 15px 40px; " +
                                "-fx-background-color: #2980b9; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;"
                )
        );

        createCVButton.setOnMouseExited(e ->
                createCVButton.setStyle(
                        "-fx-font-size: 18px; " +
                                "-fx-padding: 15px 40px; " +
                                "-fx-background-color: #3498db; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;"
                )
        );

        // Button action - Navigate to form screen
        createCVButton.setOnAction(e -> showFormScreen(null));

        // Load Saved CVs button
        Button loadCVsButton = new Button("💾 Load Saved CVs");
        loadCVsButton.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-padding: 15px 40px; " +
                        "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-cursor: hand;"
        );

        loadCVsButton.setOnMouseEntered(e ->
                loadCVsButton.setStyle(
                        "-fx-font-size: 18px; " +
                                "-fx-padding: 15px 40px; " +
                                "-fx-background-color: #229954; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;"
                )
        );

        loadCVsButton.setOnMouseExited(e ->
                loadCVsButton.setStyle(
                        "-fx-font-size: 18px; " +
                                "-fx-padding: 15px 40px; " +
                                "-fx-background-color: #27ae60; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;"
                )
        );

        loadCVsButton.setOnAction(e -> showSavedCVsScreen());

        // Create layout
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));
        centerBox.getChildren().addAll(
                titleLabel,
                welcomeLabel,
                subtitleLabel,
                dbStatsLabel,
                createCVButton,
                loadCVsButton
        );

        // Create footer
        Label footerLabel = new Label("© 2025 CV Builder by AbirHasanArko");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7; -fx-padding: 20px;");

        VBox footerBox = new VBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);

        // Main layout
        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setBottom(footerBox);
        root.setStyle("-fx-background-color: #ecf0f1;");

        // Create and set scene
        homeScene = new Scene(root, 900, 650);
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void showFormScreen(CVData cvData) {
        // Header
        String headerText = (cvData != null) ? "Edit Your CV" : "Create Your CV";
        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox headerBox = new HBox(headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #ecf0f1;");

        // Create form using GridPane
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(30));
        formGrid.setAlignment(Pos.TOP_CENTER);

        // Personal Information Section
        Label personalInfoLabel = new Label("Personal Information");
        personalInfoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        formGrid.add(personalInfoLabel, 0, 0, 2, 1);

        // ============ OBSERVABLE PATTERN - GREETING BANNER ============
        // Create a greeting label that will update dynamically
        Label greetingLabel = new Label("");
        greetingLabel.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #27ae60; " +
                        "-fx-padding: 10px; " +
                        "-fx-background-color: #d5f4e6; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-border-color: #27ae60; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5px;"
        );
        greetingLabel.setVisible(false); // Initially hidden
        greetingLabel.setManaged(false); // Don't take up space when hidden
        formGrid.add(greetingLabel, 0, 1, 2, 1);
        // =============================================================

        // Full Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setStyle("-fx-font-size: 14px;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefWidth(300);
        if (cvData != null) nameField.setText(cvData.getName());

        // ============ OBSERVABLE PATTERN - ADD LISTENER ============
        // Add a ChangeListener to observe changes in the name field
        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                if (newValue != null && !newValue.trim().isEmpty()) {
                    greetingLabel.setText("👋 Hello, " + newValue.trim() + "!");
                    greetingLabel.setVisible(true);
                    greetingLabel.setManaged(true);
                    System.out.println("Observable triggered: Name changed from '"
                            + oldValue + "' to '" + newValue + "'");
                } else {
                    greetingLabel.setVisible(false);
                    greetingLabel.setManaged(false);
                }
            }
        });
        // ===========================================================

        formGrid.add(nameLabel, 0, 2);
        formGrid.add(nameField, 1, 2);

        // Email
        Label emailLabel = new Label("Email Address:");
        emailLabel.setStyle("-fx-font-size: 14px;");
        TextField emailField = new TextField();
        emailField.setPromptText("your.email@example.com");
        emailField.setPrefWidth(300);
        if (cvData != null) emailField.setText(cvData.getEmail());
        formGrid.add(emailLabel, 0, 3);
        formGrid.add(emailField, 1, 3);

        // Phone
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setStyle("-fx-font-size: 14px;");
        TextField phoneField = new TextField();
        phoneField.setPromptText("+1 234 567 8900");
        phoneField.setPrefWidth(300);
        if (cvData != null) phoneField.setText(cvData.getPhone());
        formGrid.add(phoneLabel, 0, 4);
        formGrid.add(phoneField, 1, 4);

        // Address
        Label addressLabel = new Label("Address:");
        addressLabel.setStyle("-fx-font-size: 14px;");
        TextField addressField = new TextField();
        addressField.setPromptText("Your address");
        addressField.setPrefWidth(300);
        if (cvData != null) addressField.setText(cvData.getAddress());
        formGrid.add(addressLabel, 0, 5);
        formGrid.add(addressField, 1, 5);

        // Education Section
        Label educationLabel = new Label("Education");
        educationLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 20px 0 0 0;");
        formGrid.add(educationLabel, 0, 6, 2, 1);

        Label eduLabel = new Label("Qualifications:");
        eduLabel.setStyle("-fx-font-size: 14px;");
        TextArea educationArea = new TextArea();
        educationArea.setPromptText("E.g., Bachelor of Science in Computer Science\nUniversity Name, 2020-2024");
        educationArea.setPrefRowCount(3);
        educationArea.setPrefWidth(300);
        if (cvData != null) educationArea.setText(cvData.getEducation());
        formGrid.add(eduLabel, 0, 7);
        formGrid.add(educationArea, 1, 7);

        // Skills Section
        Label skillsLabel = new Label("Skills");
        skillsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 20px 0 0 0;");
        formGrid.add(skillsLabel, 0, 8, 2, 1);

        Label skillLabel = new Label("Your Skills:");
        skillLabel.setStyle("-fx-font-size: 14px;");
        TextArea skillsArea = new TextArea();
        skillsArea.setPromptText("E.g., Java, Python, JavaScript, JavaFX\nCommunication, Leadership");
        skillsArea.setPrefRowCount(3);
        skillsArea.setPrefWidth(300);
        if (cvData != null) skillsArea.setText(cvData.getSkills());
        formGrid.add(skillLabel, 0, 9);
        formGrid.add(skillsArea, 1, 9);

        // Work Experience Section
        Label workLabel = new Label("Work Experience");
        workLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 20px 0 0 0;");
        formGrid.add(workLabel, 0, 10, 2, 1);

        Label workExpLabel = new Label("Experience:");
        workExpLabel.setStyle("-fx-font-size: 14px;");
        TextArea workArea = new TextArea();
        workArea.setPromptText("E.g., Software Developer Intern\nCompany Name, June 2023 - Aug 2023\n- Developed features using Java");
        workArea.setPrefRowCount(4);
        workArea.setPrefWidth(300);
        if (cvData != null) workArea.setText(cvData.getWorkExperience());
        formGrid.add(workExpLabel, 0, 11);
        formGrid.add(workArea, 1, 11);

        // Projects Section
        Label projectsLabel = new Label("Projects");
        projectsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 20px 0 0 0;");
        formGrid.add(projectsLabel, 0, 12, 2, 1);

        Label projectLabel = new Label("Projects:");
        projectLabel.setStyle("-fx-font-size: 14px;");
        TextArea projectsArea = new TextArea();
        projectsArea.setPromptText("E.g., CV Builder Application\n- Built using JavaFX\n- Features include...");
        projectsArea.setPrefRowCount(4);
        projectsArea.setPrefWidth(300);
        if (cvData != null) projectsArea.setText(cvData.getProjects());
        formGrid.add(projectLabel, 0, 13);
        formGrid.add(projectsArea, 1, 13);

        // Set current CV ID if editing
        if (cvData != null) {
            currentCVId = cvData.getId();
        }

        // Buttons
        Button backButton = new Button("← Back to Home");
        backButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #95a5a6; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        Button generateButton = new Button("Generate CV");
        generateButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 30px; " +
                        "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        generateButton.setOnAction(e -> {
            // Validate fields
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Information");
                alert.setHeaderText("Required fields are empty");
                alert.setContentText("Please fill in at least Name and Email to generate your CV.");
                alert.showAndWait();
            } else {
                showPreviewScreen(nameField.getText(), emailField.getText(), phoneField.getText(),
                        addressField.getText(), educationArea.getText(), skillsArea.getText(),
                        workArea.getText(), projectsArea.getText());
            }
        });

        HBox buttonBox = new HBox(15, backButton, generateButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        // Wrap form in ScrollPane
        ScrollPane scrollPane = new ScrollPane(formGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

        // Main layout
        BorderPane formRoot = new BorderPane();
        formRoot.setTop(headerBox);
        formRoot.setCenter(scrollPane);
        formRoot.setBottom(buttonBox);
        formRoot.setStyle("-fx-background-color: white;");

        formScene = new Scene(formRoot, 900, 650);
        primaryStage.setScene(formScene);
    }

    private void showPreviewScreen(String name, String email, String phone, String address,
                                   String education, String skills, String work, String projects) {

        // Store current CV data
        currentName = name;
        currentEmail = email;
        currentPhone = phone;
        currentAddress = address;
        currentEducation = education;
        currentSkills = skills;
        currentWork = work;
        currentProjects = projects;

        // Create CV Preview Layout
        VBox cvLayout = new VBox(20);
        cvLayout.setPadding(new Insets(40, 60, 40, 60));
        cvLayout.setStyle("-fx-background-color: white;");

        // Header with name
        Label nameLabel = new Label(name.toUpperCase());
        nameLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Contact Information
        VBox contactBox = new VBox(5);
        if (!email.isEmpty()) {
            Label emailLabel = new Label("📧 " + email);
            emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
            contactBox.getChildren().add(emailLabel);
        }
        if (!phone.isEmpty()) {
            Label phoneLabel = new Label("📱 " + phone);
            phoneLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
            contactBox.getChildren().add(phoneLabel);
        }
        if (!address.isEmpty()) {
            Label addressLabel = new Label("📍 " + address);
            addressLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
            contactBox.getChildren().add(addressLabel);
        }

        // Separator line
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #3498db; -fx-pref-height: 3px;");

        cvLayout.getChildren().addAll(nameLabel, contactBox, separator1);

        // Education Section
        if (!education.isEmpty()) {
            Label educationHeader = new Label("EDUCATION");
            educationHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 10px 0 5px 0;");

            Label educationContent = new Label(education);
            educationContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-wrap-text: true;");
            educationContent.setWrapText(true);
            educationContent.setMaxWidth(700);

            cvLayout.getChildren().addAll(educationHeader, educationContent);
        }

        // Skills Section
        if (!skills.isEmpty()) {
            Label skillsHeader = new Label("SKILLS");
            skillsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 10px 0 5px 0;");

            Label skillsContent = new Label(skills);
            skillsContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-wrap-text: true;");
            skillsContent.setWrapText(true);
            skillsContent.setMaxWidth(700);

            cvLayout.getChildren().addAll(skillsHeader, skillsContent);
        }

        // Work Experience Section
        if (!work.isEmpty()) {
            Label workHeader = new Label("WORK EXPERIENCE");
            workHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 10px 0 5px 0;");

            Label workContent = new Label(work);
            workContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-wrap-text: true;");
            workContent.setWrapText(true);
            workContent.setMaxWidth(700);

            cvLayout.getChildren().addAll(workHeader, workContent);
        }

        // Projects Section
        if (!projects.isEmpty()) {
            Label projectsHeader = new Label("PROJECTS");
            projectsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 10px 0 5px 0;");

            Label projectsContent = new Label(projects);
            projectsContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-wrap-text: true;");
            projectsContent.setWrapText(true);
            projectsContent.setMaxWidth(700);

            cvLayout.getChildren().addAll(projectsHeader, projectsContent);
        }

        // Footer
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #3498db; -fx-pref-height: 2px;");
        Label footerLabel = new Label("Generated by CV Builder by AbirHasanArko | © 2025");
        footerLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6; -fx-padding: 10px 0 0 0;");

        cvLayout.getChildren().addAll(separator2, footerLabel);

        // Wrap in ScrollPane
        ScrollPane scrollPane = new ScrollPane(cvLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

        // Buttons
        Button backToFormButton = new Button("← Edit CV");
        backToFormButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #95a5a6; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        backToFormButton.setOnAction(e -> primaryStage.setScene(formScene));

        // Save CV Button
        Button saveCVButton = new Button("💾 Save CV");
        saveCVButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #f39c12; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        saveCVButton.setOnAction(e -> saveCVToDatabase());

        Button backToHomeButton = new Button("🏠 Back to Home");
        backToHomeButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        backToHomeButton.setOnAction(e -> primaryStage.setScene(homeScene));

        HBox buttonBox = new HBox(15, backToFormButton, saveCVButton, backToHomeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle("-fx-background-color: #ecf0f1;");

        // Header
        Label previewHeader = new Label("Your CV Preview");
        previewHeader.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        HBox headerBox = new HBox(previewHeader);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #ecf0f1;");

        // Main layout
        BorderPane previewRoot = new BorderPane();
        previewRoot.setTop(headerBox);
        previewRoot.setCenter(scrollPane);
        previewRoot.setBottom(buttonBox);

        previewScene = new Scene(previewRoot, 900, 650);
        primaryStage.setScene(previewScene);
    }

    private void saveCVToDatabase() {
        CVData cvData = new CVData(currentName, currentEmail, currentPhone, currentAddress,
                currentEducation, currentSkills, currentWork, currentProjects);

        boolean success;
        String message;

        if (currentCVId != null) {
            // Update existing CV
            cvData.setId(currentCVId);
            success = dbManager.updateCV(cvData);
            message = success ? "CV updated successfully!" : "Failed to update CV!";
        } else {
            // Add new CV
            int id = dbManager.addCV(cvData);
            success = id > 0;
            message = success ? "CV saved successfully! (ID: " + id + ")" : "Failed to save CV!";
            if (success) {
                currentCVId = id;
            }
        }

        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(success ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSavedCVsScreen() {
        // Header
        Label headerLabel = new Label("Saved CVs");
        headerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox headerBox = new HBox(headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #ecf0f1;");

        // Table View
        TableView<CVData> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID Column
        TableColumn<CVData, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(50);

        // Name Column
        TableColumn<CVData, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Email Column
        TableColumn<CVData, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Phone Column
        TableColumn<CVData, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Updated At Column
        TableColumn<CVData, String> updatedCol = new TableColumn<>("Last Updated");
        updatedCol.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, updatedCol);

        // Load data
        List<CVData> cvList = dbManager.getAllCVs();
        ObservableList<CVData> data = FXCollections.observableArrayList(cvList);
        table.setItems(data);

        // Buttons
        Button backButton = new Button("← Back to Home");
        backButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #95a5a6; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        backButton.setOnAction(e -> showHomeScreen());

        Button viewButton = new Button("👁️ View");
        viewButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        viewButton.setOnAction(e -> {
            CVData selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showPreviewScreen(selected.getName(), selected.getEmail(), selected.getPhone(),
                        selected.getAddress(), selected.getEducation(), selected.getSkills(),
                        selected.getWorkExperience(), selected.getProjects());
                currentCVId = selected.getId();
            } else {
                showAlert("No Selection", "Please select a CV to view.", Alert.AlertType.WARNING);
            }
        });

        Button editButton = new Button("✏️ Edit");
        editButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #f39c12; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        editButton.setOnAction(e -> {
            CVData selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showFormScreen(selected);
            } else {
                showAlert("No Selection", "Please select a CV to edit.", Alert.AlertType.WARNING);
            }
        });

        Button deleteButton = new Button("🗑️ Delete");
        deleteButton.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5px;"
        );
        deleteButton.setOnAction(e -> {
            CVData selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Delete");
                confirmAlert.setHeaderText("Delete CV: " + selected.getName());
                confirmAlert.setContentText("Are you sure you want to delete this CV?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean success = dbManager.deleteCV(selected.getId());
                    if (success) {
                        data.remove(selected);
                        showAlert("Success", "CV deleted successfully!", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to delete CV!", Alert.AlertType.ERROR);
                    }
                }
            } else {
                showAlert("No Selection", "Please select a CV to delete.", Alert.AlertType.WARNING);
            }
        });

        HBox buttonBox = new HBox(15, backButton, viewButton, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setStyle("-fx-background-color: #ecf0f1;");

        // Main layout
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(table);
        root.setBottom(buttonBox);
        root.setStyle("-fx-background-color: white;");

        savedCVsScene = new Scene(root, 900, 650);
        primaryStage.setScene(savedCVsScene);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}