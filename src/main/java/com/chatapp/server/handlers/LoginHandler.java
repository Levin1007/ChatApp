package com.chatapp.server.handlers;

import com.chatapp.model.User;
import com.chatapp.server.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.*;

/**
 * POST /login
 * Body: { "username": "alice", "password": "pass123" }
 * Returns the User (id + username) on success, 401 on bad credentials.
 */
public class LoginHandler extends BaseHandler implements HttpHandler {

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

        if (req == null || !req.has("username") || !req.has("password")) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        String username = req.get("username").getAsString().trim();
        String password = req.get("password").getAsString().trim();

        String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"));
                sendResponse(exchange, 200, gson.toJson(user));
                System.out.println("[LOGIN] " + username);
            } else {
                sendError(exchange, 401, "Invalid credentials");
            }

        } catch (SQLException e) {
            System.err.println("[LoginHandler] " + e.getMessage());
            sendError(exchange, 500, "Server error");
        }
    }
}
