package com.chatapp.server.handlers;

import com.chatapp.model.Message;
import com.chatapp.server.DatabaseManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GET /messages?user1=1&user2=2
 * Returns all messages exchanged between user1 and user2, ordered by timestamp.
 */
public class MessagesHandler extends BaseHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        // Parse ?user1=X&user2=Y from the URL
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isBlank()) {
            sendError(exchange, 400, "Missing query parameters user1 and user2");
            return;
        }

        int user1 = 0, user2 = 0;
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                if (kv[0].equals("user1")) user1 = Integer.parseInt(kv[1]);
                if (kv[0].equals("user2")) user2 = Integer.parseInt(kv[1]);
            }
        }

        if (user1 == 0 || user2 == 0) {
            sendError(exchange, 400, "Invalid user IDs");
            return;
        }

        // Fetch messages in both directions, joined with the sender's username
        String sql = """
            SELECT m.id, m.sender_id, m.receiver_id, m.content,
                   FORMATDATETIME(m.timestamp, 'HH:mm') AS timestamp,
                   u.username AS sender_name
            FROM messages m
            JOIN users u ON m.sender_id = u.id
            WHERE (m.sender_id = ? AND m.receiver_id = ?)
               OR (m.sender_id = ? AND m.receiver_id = ?)
            ORDER BY m.timestamp ASC
            """;

        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user1); stmt.setInt(2, user2);
            stmt.setInt(3, user2); stmt.setInt(4, user1);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getInt("id"));
                msg.setSenderId(rs.getInt("sender_id"));
                msg.setReceiverId(rs.getInt("receiver_id"));
                msg.setContent(rs.getString("content"));
                msg.setTimestamp(rs.getString("timestamp"));
                msg.setSenderName(rs.getString("sender_name"));
                messages.add(msg);
            }

            sendResponse(exchange, 200, gson.toJson(messages));

        } catch (SQLException e) {
            System.err.println("[MessagesHandler] " + e.getMessage());
            sendError(exchange, 500, "Server error");
        }
    }
}
