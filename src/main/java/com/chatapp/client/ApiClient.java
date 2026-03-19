package com.chatapp.client;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * ApiClient is the single place where all HTTP requests to the server are made.
 * Every public method corresponds to one server endpoint.
 */
public class ApiClient {

    private static final String     BASE_URL   = "http://localhost:8080";
    private static final HttpClient HTTP       = HttpClient.newHttpClient();
    private static final Gson       GSON       = new Gson();

    // ─── Private helpers ─────────────────────────────────────────────────────

    /** Sends a POST request with a JSON body. Returns response body, or null on failure. */
    private static String post(String path, String json) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200 ? res.body() : null;

        } catch (Exception e) {
            System.err.println("[ApiClient POST] " + path + " → " + e.getMessage());
            return null;
        }
    }

    /** Sends a GET request. Returns response body, or null on failure. */
    private static String get(String path) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .GET()
                    .build();

            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200 ? res.body() : null;

        } catch (Exception e) {
            System.err.println("[ApiClient GET] " + path + " → " + e.getMessage());
            return null;
        }
    }

    // ─── Public API methods ───────────────────────────────────────────────────

    /**
     * Registers a new user.
     * @return true if registration succeeded, false if username already taken.
     */
    public static boolean register(String username, String password) {
        String json = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                escape(username), escape(password));
        return post("/register", json) != null;
    }

    /**
     * Logs in with the given credentials.
     * @return the User object (id + username) on success, null on failure.
     */
    public static User login(String username, String password) {
        String json = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                escape(username), escape(password));
        String body = post("/login", json);
        return body != null ? GSON.fromJson(body, User.class) : null;
    }

    /**
     * Fetches all registered users.
     * @return list of Users (never null).
     */
    public static List<User> getUsers() {
        String body = get("/users");
        if (body == null) return List.of();
        return GSON.fromJson(body, new TypeToken<List<User>>() {}.getType());
    }

    /**
     * Sends a message from one user to another.
     * @return true if the server accepted it.
     */
    public static boolean sendMessage(int senderId, int receiverId, String content) {
        String json = String.format(
                "{\"senderId\":%d,\"receiverId\":%d,\"content\":\"%s\"}",
                senderId, receiverId, escape(content));
        return post("/sendMessage", json) != null;
    }

    /**
     * Retrieves the full conversation between two users.
     * @return list of Messages ordered by time (never null).
     */
    public static List<Message> getMessages(int user1Id, int user2Id) {
        String body = get("/messages?user1=" + user1Id + "&user2=" + user2Id);
        if (body == null) return List.of();
        return GSON.fromJson(body, new TypeToken<List<Message>>() {}.getType());
    }

    /** Escapes double quotes and backslashes so strings are safe inside JSON literals. */
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
