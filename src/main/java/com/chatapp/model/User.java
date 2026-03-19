package com.chatapp.model;

/**
 * Represents one row in the 'users' table.
 * Gson uses this class to convert between Java objects and JSON.
 */
public class User {

    private int    id;
    private String username;
    private String password; // never sent back to clients

    /** No-args constructor required by Gson. */
    public User() {}

    /** Constructor used when returning logged-in user data (no password). */
    public User(int id, String username) {
        this.id       = id;
        this.username = username;
    }

    public int    getId()                       { return id; }
    public void   setId(int id)                 { this.id = id; }

    public String getUsername()                 { return username; }
    public void   setUsername(String username)  { this.username = username; }

    public String getPassword()                 { return password; }
    public void   setPassword(String password)  { this.password = password; }

    @Override
    public String toString() { return username; }
}
