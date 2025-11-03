package model;

import java.sql.Timestamp;

public class ChatRoom {
    private long id;
    private long productsId;
    private long buyerId;
    private Timestamp createdAt;

    public ChatRoom() {}

    public ChatRoom(long id, long productsId, long buyerId, Timestamp createdAt) {
        this.id = id;
        this.productsId = productsId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getProductsId() { return productsId; }
    public long getBuyerId() { return buyerId; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(long id) { this.id = id; }
    public void setProductsId(long productsId) { this.productsId = productsId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
