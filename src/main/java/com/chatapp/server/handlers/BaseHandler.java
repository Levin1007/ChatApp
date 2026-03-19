package com.chatapp.server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * BaseHandler provides shared helper methods used by every endpoint handler.
 */
public abstract class BaseHandler {

    /**
     * Reads the full HTTP request body and returns it as a UTF-8 String.
     * Used for POST requests that carry a JSON body.
     */
    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Sends a JSON response to the client.
     *
     * @param exchange   the active HTTP exchange
     * @param statusCode 200 OK | 400 Bad Request | 401 Unauthorized | 500 Server Error
     * @param json       the JSON string to return
     */
    protected void sendResponse(HttpExchange exchange, int statusCode, String json)
            throws IOException {

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Convenience wrapper for a JSON error payload. */
    protected void sendError(HttpExchange exchange, int statusCode, String message)
            throws IOException {
        sendResponse(exchange, statusCode,
                String.format("{\"error\":\"%s\"}", message.replace("\"", "\\\"")));
    }
}
