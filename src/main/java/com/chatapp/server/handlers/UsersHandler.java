package com.chatapp.server.handlers;

import com.chatapp.model.User;
import com.chatapp.server.DatabaseManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GET /users
 * Returns a JSON array of all registered users (id + username only, no passwords).
 */
public class UsersHandler extends BaseHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String sql = "SELECT id, username FROM users ORDER BY username ASC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("username")));
            }

            sendResponse(exchange, 200, gson.toJson(users));

        } catch (SQLException e) {
            System.err.println("[UsersHandler] " + e.getMessage());
            sendError(exchange, 500, "Server error");
        }
    }
}
