package model;

import java.sql.Timestamp;

public class Message {
    private long id;
    private long chatRoomId;
    private long senderId;
    private String message;
    private Timestamp createdAt;

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(long chatRoomId) { this.chatRoomId = chatRoomId; }

    public long getSenderId() { return senderId; }
    public void setSenderId(long senderId) { this.senderId = senderId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
