package com.chatapp.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hashes passwords using SHA-256 before storing them in the database.
 * The hash is a one-way function — the original password cannot be recovered.
 */
public class PasswordUtils {

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());

            // Convert byte array to hex string (e.g. "a3f1bc...")
            StringBuilder hex = new StringBuilder();
            for (byte b : hashed) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}