package model;

import java.sql.Timestamp;

public class ChatRoomDisplayDTO {

    private int id;
    private int productId;
    private int buyerId;
    private int sellerId;        
    private String productStatus; 
    private Timestamp createdAt;
    private String productTitle;
    private String otherUserNickname;
    private int otherUserId;
    private boolean rated; 
    private Integer ratingId;
    
    public ChatRoomDisplayDTO(int id,
                              int productId,
                              int buyerId,
                              int sellerId,
                              String productStatus,
                              Timestamp createdAt,
                              String productTitle,
                              String otherUserNickname,
                              int otherUserId) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productStatus = productStatus;
        this.createdAt = createdAt;
        this.productTitle = productTitle;
        this.otherUserNickname = otherUserNickname;
        this.otherUserId = otherUserId;
    }

    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getBuyerId() { return buyerId; }
    public int getSellerId() { return sellerId; }
    public String getProductStatus() { return productStatus; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getProductTitle() { return productTitle; }
    public String getOtherUserNickname() { return otherUserNickname; }
    public int getOtherUserId() { return otherUserId; }
    public boolean isRated() { return rated; }
    public Integer getRatingId() { return ratingId; }
    
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public void setProductStatus(String productStatus) { this.productStatus = productStatus; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public void setOtherUserNickname(String otherUserNickname) { this.otherUserNickname = otherUserNickname; }
    public void setOtherUserId(int otherUserId) { this.otherUserId = otherUserId; }
    public void setRated(boolean rated) { this.rated = rated; }
    public void setRatingId(Integer ratingId) { this.ratingId = ratingId; }
}
