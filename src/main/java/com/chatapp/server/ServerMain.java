package com.chatapp.server;

import com.chatapp.server.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

/**
 * Entry point for the server.
 * Run this FIRST, before launching any client.
 */
public class ServerMain {

    public static void main(String[] args) throws Exception {

        // 1. Create tables if they don't exist yet
        DatabaseManager.initializeDatabase();

        org.h2.tools.Server.createWebServer("-webPort", "8082").start();

        // 2. Create the HTTP server bound to port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // 3. Register each API endpoint → handler
        server.createContext("/register",    new RegisterHandler());
        server.createContext("/login",       new LoginHandler());
        server.createContext("/users",       new UsersHandler());
        server.createContext("/sendMessage", new SendMessageHandler());
        server.createContext("/messages",    new MessagesHandler());

        // 4. Start — uses the default (cached thread pool) executor
        server.setExecutor(null);
        server.start();

        System.out.println("[SERVER] Press Ctrl+C to stop.");
    }
}
