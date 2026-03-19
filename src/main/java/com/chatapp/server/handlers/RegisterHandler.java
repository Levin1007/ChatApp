package com.chatapp.server.handlers;

import com.chatapp.server.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.*;

/**
 * POST /register
 * Body: { "username": "alice", "password": "pass123" }
 * Returns 200 on success, 400 if username taken or fields empty, 500 on DB error.
 */
public class RegisterHandler extends BaseHandler implements HttpHandler {

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
            sendError(exchange, 400, "Missing username or password");
            return;
        }

        String username = req.get("username").getAsString().trim();
        String password = req.get("password").getAsString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            sendError(exchange, 400, "Fields cannot be empty");
            return;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // NOTE: hash in production (see improvements)
            stmt.executeUpdate();

            sendResponse(exchange, 200, "{\"message\":\"Registration successful\"}");
            System.out.println("[REGISTER] New user: " + username);

        } catch (SQLIntegrityConstraintViolationException e) {
            sendError(exchange, 400, "Username already taken");
        } catch (SQLException e) {
            System.err.println("[RegisterHandler] " + e.getMessage());
            sendError(exchange, 500, "Server error");
        }
    }
}
