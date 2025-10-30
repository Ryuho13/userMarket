package model;

import java.sql.Timestamp;

public class ChatRoom {
    private long id;
    private long productsId;
    private long buyerId;
    private Timestamp createdAt;

    // 기본 생성자
    public ChatRoom() {}

    // 전체 생성자
    public ChatRoom(long id, long productsId, long buyerId, Timestamp createdAt) {
        this.id = id;
        this.productsId = productsId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
    }

    // Getter/Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getProductsId() { return productsId; }
    public void setProductsId(long productsId) { this.productsId = productsId; }

    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
