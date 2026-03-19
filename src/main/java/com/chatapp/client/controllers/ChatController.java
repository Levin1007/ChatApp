package com.chatapp.client.controllers;

import com.chatapp.client.ApiClient;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller for chat.fxml.
 * Manages the user list, message history, sending messages, and auto-refresh.
 */
public class ChatController {

    @FXML private ListView<User>    userListView;
    @FXML private ListView<Message> messageListView;
    @FXML private TextField         messageInput;
    @FXML private Label             chatHeaderLabel;
    @FXML private Label             currentUserLabel;

    private User       currentUser;
    private User       selectedUser;

    // ─── Initialization ───────────────────────────────────────────────────────

    /**
     * Called by LoginController immediately after loading chat.fxml.
     * Sets up the user list and starts auto-refresh.
     */
    public void initializeUser(User user) {
        this.currentUser = user;
        currentUserLabel.setText("Logged in as: " + user.getUsername());

        // Custom cell factory: show only the username string in the user list
        userListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText((empty || u == null) ? null : u.getUsername());
            }
        });

        // Custom cell factory: format each message bubble
        messageListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Message m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean mine = m.getSenderId() == currentUser.getId();
                    String time  = m.getTimestamp() != null ? m.getTimestamp() : "";
                    setText((mine ? "You" : m.getSenderName()) + "  [" + time + "]\n" + m.getContent());
                    setStyle(mine
                            ? "-fx-background-color: #1A4E78; -fx-text-fill: white;"
                              + "-fx-padding: 6 10; -fx-background-radius: 8;"
                            : "-fx-background-color: #2C3E50; -fx-text-fill: #ECF0F1;"
                              + "-fx-padding: 6 10; -fx-background-radius: 8;");
                }
            }
        });

        // When a user in the sidebar is selected, load that conversation
        userListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldUser, newUser) -> {
                    if (newUser != null) {
                        selectedUser = newUser;
                        chatHeaderLabel.setText("Chat with " + newUser.getUsername());
                        loadMessages();
                    }
                }
        );

        loadUsers();
        startAutoRefresh();
    }

    // ─── Data loading ─────────────────────────────────────────────────────────

    private void loadUsers() {
        List<User> all = ApiClient.getUsers();
        // Remove yourself from the list
        List<User> others = all.stream()
                .filter(u -> u.getId() != currentUser.getId())
                .toList();

        Platform.runLater(() ->
                userListView.setItems(FXCollections.observableArrayList(others)));
    }

    private void loadMessages() {
        if (selectedUser == null) return;

        List<Message> msgs = ApiClient.getMessages(currentUser.getId(), selectedUser.getId());

        Platform.runLater(() -> {
            messageListView.setItems(FXCollections.observableArrayList(msgs));
            if (!msgs.isEmpty()) {
                messageListView.scrollTo(msgs.size() - 1);
            }
        });
    }

    // ─── Event handlers ───────────────────────────────────────────────────────

    /** Called by the Send button and by pressing Enter in the message field. */
    @FXML
    private void handleSend() {
        if (selectedUser == null) {
            chatHeaderLabel.setText("⬅ Select a user first!");
            return;
        }
        String content = messageInput.getText().trim();
        if (content.isEmpty()) return;

        boolean ok = ApiClient.sendMessage(currentUser.getId(), selectedUser.getId(), content);
        if (ok) {
            messageInput.clear();
            loadMessages();
        }
    }

    /** Manual refresh button. */
    @FXML
    private void handleRefresh() {
        loadUsers();
        loadMessages();
    }

    // ─── Auto-refresh ─────────────────────────────────────────────────────────

    /**
     * Polls the server every 3 seconds to pick up new messages automatically.
     * Uses JavaFX ScheduledService so it runs safely off the UI thread.
     */
    private void startAutoRefresh() {
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        loadMessages();
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.seconds(3));
        service.start();
    }
}
