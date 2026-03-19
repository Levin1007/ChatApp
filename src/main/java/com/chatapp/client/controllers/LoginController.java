package com.chatapp.client.controllers;

import com.chatapp.client.ApiClient;
import com.chatapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for login.fxml.
 * Handles login and registration button events.
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         statusLabel;

    /** Called when the "Log In" button is pressed. */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all fields.", false);
            return;
        }

        User user = ApiClient.login(username, password);

        if (user == null) {
            showStatus("Invalid username or password.", false);
        } else {
            switchToChat(user);
        }
    }

    /** Called when the "Register" button is pressed. */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all fields.", false);
            return;
        }

        boolean ok = ApiClient.register(username, password);

        if (ok) {
            showStatus("Account created! You can now log in.", true);
        } else {
            showStatus("Username already taken — choose another.", false);
        }
    }

    /** Replaces the current scene with the chat screen, passing in the logged-in user. */
    private void switchToChat(User loggedInUser) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatapp/client/views/chat.fxml"));
            Parent root = loader.load();

            ChatController chatCtrl = loader.getController();
            chatCtrl.initializeUser(loggedInUser);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 760, 520));
            stage.setTitle("Chat — " + loggedInUser.getUsername());
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            showStatus("Error loading chat: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #27AE60; -fx-font-size: 12px;"
                : "-fx-text-fill: #E74C3C; -fx-font-size: 12px;");
    }
}
