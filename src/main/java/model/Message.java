package model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int chatRoomId;
    private int senderId;
    private String message;
    private Timestamp createdAt;

    // 기본 생성자
    public Message() {}

    // 생성자 (DB 조회용)
    public Message(int id, int chatRoomId, int senderId, String message, Timestamp createdAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // 생성자 (새 메시지 저장용)
    public Message(int chatRoomId, int senderId, String message) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getter / Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(int chatRoomId) { this.chatRoomId = chatRoomId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
