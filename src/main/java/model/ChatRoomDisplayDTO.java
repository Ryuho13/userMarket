package model;

import java.sql.Timestamp;

public class ChatRoomDisplayDTO {
    private long id;
    private long productId;
    private long buyerId;
    private Timestamp createdAt;
    private String productTitle;
    private String otherUserNickname;
    private long otherUserId;

    public ChatRoomDisplayDTO(long id, long productId, long buyerId, Timestamp createdAt, String productTitle, String otherUserNickname, long otherUserId) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.productTitle = productTitle;
        this.otherUserNickname = otherUserNickname;
        this.otherUserId = otherUserId;
    }

    // Getters
    public long getId() { return id; }
    public long getProductId() { return productId; }
    public long getBuyerId() { return buyerId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getProductTitle() { return productTitle; }
    public String getOtherUserNickname() { return otherUserNickname; }
    public long getOtherUserId() { return otherUserId; }

    // Setters (if needed, but for DTOs, often only getters are used)
    public void setId(long id) { this.id = id; }
    public void setProductId(long productId) { this.productId = productId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public void setOtherUserNickname(String otherUserNickname) { this.otherUserNickname = otherUserNickname; }
    public void setOtherUserId(long otherUserId) { this.otherUserId = otherUserId; }
}