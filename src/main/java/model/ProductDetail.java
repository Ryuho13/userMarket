package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDetail {
    private int id;
    private int sellerId;
    private int categoryId;
    private String title;
    private String status;
    private int sellPrice;
    private String description;
    private Timestamp createdAt;
    private int viewCount;
    private String sidoName;
    private String regionName;
    private String sellerName;
    private String sellerMobile;
    private Double sellerRating;     
    private Integer sellerRatingCount;
    private List<String> images = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSellPrice() { return sellPrice; }
    public void setSellPrice(int sellPrice) { this.sellPrice = sellPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public String getSidoName() { return sidoName; }
    public void setSidoName(String sidoName) { this.sidoName = sidoName; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerMobile() { return sellerMobile; }
    public void setSellerMobile(String sellerMobile) { this.sellerMobile = sellerMobile; }

    public Double getSellerRating() { return sellerRating; }
    public void setSellerRating(Double sellerRating) { this.sellerRating = sellerRating; }

    public Integer getSellerRatingCount() { return sellerRatingCount; }
    public void setSellerRatingCount(Integer sellerRatingCount) { this.sellerRatingCount = sellerRatingCount; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
