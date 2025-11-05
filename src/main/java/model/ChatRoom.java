package model;

import java.sql.Timestamp;

public class ChatRoom {
    private long id;
    private long productId;
    private long buyerId;
    private Timestamp createdAt;

    // 생성자1: DB 조회 시 (모든 컬럼 포함)
    public ChatRoom(long id, long productId, long buyerId, Timestamp createdAt) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
    }

    // 생성자2: 새로 생성할 때 (createdAt 자동 생성)
    public ChatRoom(long id, long productId, long buyerId) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getter / Setter
    public long getId() { return id; }
    public long getProductId() { return productId; }
    public long getBuyerId() { return buyerId; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(long id) { this.id = id; }
    public void setProductId(long productId) { this.productId = productId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
