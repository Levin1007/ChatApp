package com.chatapp.server.handlers;

import com.chatapp.server.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.*;

/**
 * POST /sendMessage
 * Body: { "senderId": 1, "receiverId": 2, "content": "Hello!" }
 * Inserts the message into the database.
 */
public class SendMessageHandler extends BaseHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = readRequestBody(exchange);
        JsonObject req;
        try {
            req = gson.fromJson(body, JsonObject.class);
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid JSON");
            return;
        }

        if (req == null || !req.has("senderId") || !req.has("receiverId") || !req.has("content")) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        int    senderId   = req.get("senderId").getAsInt();
        int    receiverId = req.get("receiverId").getAsInt();
        String content    = req.get("content").getAsString().trim();

        if (content.isEmpty()) {
            sendError(exchange, 400, "Message cannot be empty");
            return;
        }

        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();

            sendResponse(exchange, 200, "{\"message\":\"Sent\"}");

        } catch (SQLException e) {
            System.err.println("[SendMessageHandler] " + e.getMessage());
            sendError(exchange, 500, "Server error");
        }
    }
}
