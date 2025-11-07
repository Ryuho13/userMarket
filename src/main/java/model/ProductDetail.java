package model;

import java.util.List;

public class ProductDetail {
    private int id;
    private String title;
    private String description;
    private int sellPrice;
    private int sellerId;
    private String status;
    private List<String> images;
    private String sellerMobile;
    private Double sellerRating;
    private String sellerSigg;
    private int categoryId;  // ✅ 추가

    public ProductDetail() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSellPrice() { return sellPrice; }
    public void setSellPrice(int sellPrice) { this.sellPrice = sellPrice; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getSellerMobile() { return sellerMobile; }
    public void setSellerMobile(String sellerMobile) { this.sellerMobile = sellerMobile; }

    public Double getSellerRating() { return sellerRating; }
    public void setSellerRating(Double sellerRating) { this.sellerRating = sellerRating; }

    public String getSellerSigg() { return sellerSigg; }
    public void setSellerSigg(String sellerSigg) { this.sellerSigg = sellerSigg; }

    public int getCategoryId() { return categoryId; }     // ✅ 추가
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; } // ✅ 추가
}
