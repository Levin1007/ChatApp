package com.chatapp.model;

/**
 * Represents one row in the 'messages' table.
 * senderName is not stored in the DB — it is resolved via a JOIN in MessagesHandler.
 */
public class Message {

    private int    id;
    private int    senderId;
    private int    receiverId;
    private String content;
    private String timestamp;
    private String senderName; // populated by server JOIN, not a DB column

    public Message() {}

    public int    getId()                           { return id; }
    public void   setId(int id)                     { this.id = id; }

    public int    getSenderId()                     { return senderId; }
    public void   setSenderId(int senderId)         { this.senderId = senderId; }

    public int    getReceiverId()                   { return receiverId; }
    public void   setReceiverId(int receiverId)     { this.receiverId = receiverId; }

    public String getContent()                      { return content; }
    public void   setContent(String content)        { this.content = content; }

    public String getTimestamp()                    { return timestamp; }
    public void   setTimestamp(String timestamp)    { this.timestamp = timestamp; }

    public String getSenderName()                   { return senderName; }
    public void   setSenderName(String senderName)  { this.senderName = senderName; }
}
