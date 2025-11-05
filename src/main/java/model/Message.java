package model;

import java.sql.Timestamp;

public class Message {
    private long id;
    private long chatRoomId;
    private long senderId;
    private String message;
    private Timestamp createdAt;

    // 기본 생성자
    public Message() {}

    // 생성자 (DB 조회용)
    public Message(long id, long chatRoomId, long senderId, String message, Timestamp createdAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // 생성자 (새 메시지 저장용)
    public Message(long chatRoomId, long senderId, String message) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getter / Setter
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
