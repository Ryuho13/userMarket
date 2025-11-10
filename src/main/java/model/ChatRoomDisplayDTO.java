package model;

import java.sql.Timestamp;

public class ChatRoomDisplayDTO {
    private int id;
    private int productId;
    private int buyerId;
    private Timestamp createdAt;
    private String productTitle;
    private String otherUserNickname;
    private int otherUserId;

    public ChatRoomDisplayDTO(int id, int productId, int buyerId, Timestamp createdAt, String productTitle, String otherUserNickname, int otherUserId) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.productTitle = productTitle;
        this.otherUserNickname = otherUserNickname;
        this.otherUserId = otherUserId;
    }

    // Getters
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getBuyerId() { return buyerId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getProductTitle() { return productTitle; }
    public String getOtherUserNickname() { return otherUserNickname; }
    public int getOtherUserId() { return otherUserId; }

    // Setters (if needed, but for DTOs, often only getters are used)
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public void setOtherUserNickname(String otherUserNickname) { this.otherUserNickname = otherUserNickname; }
    public void setOtherUserId(int otherUserId) { this.otherUserId = otherUserId; }
}