package model;

import java.sql.Timestamp;

public class ChatRoom {
    private int id;
    private int productId;
    private int buyerId;
    private Timestamp createdAt;

    // 생성자1: DB 조회 시 (모든 컬럼 포함)
    public ChatRoom(int id, int productId, int buyerId, Timestamp createdAt) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
    }

    // 생성자2: 새로 생성할 때 (createdAt 자동 생성)
    public ChatRoom(int id, int productId, int buyerId) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getter / Setter
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getBuyerId() { return buyerId; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
