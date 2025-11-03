package model;

import java.sql.Timestamp;

public class Message {
    private long id;
    private long chatRoomId;
    private long senderId;
    private String message;
    private Timestamp createdAt;

    public Message() {}

    public Message(long id, long chatRoomId, long senderId, String message, Timestamp createdAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getChatRoomId() { return chatRoomId; }
    public long getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(long id) { this.id = id; }
    public void setChatRoomId(long chatRoomId) { this.chatRoomId = chatRoomId; }
    public void setSenderId(long senderId) { this.senderId = senderId; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
