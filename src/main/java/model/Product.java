package model;

public class Product {
    private int id;            
    private String title;      
    private int sellPrice;     
    private String siggName;   
    private String imgName;    
    private String displayImg;  
    private int viewCount;     
    private String status;      
    private String description; 
    private int categoryId;    
    private int sidoId;      
    private int regionId; 
    private String imgSrc;  
    private int sellerId;
    public Product() {}
    public Product(int id, String title, int sellPrice,
                   String siggName, String displayImg,
                   int viewCount, String status) {
        this.id = id;
        this.title = title;
        this.sellPrice = sellPrice;
        this.siggName = siggName;
        this.displayImg = displayImg;
        this.viewCount = viewCount;
        this.status = status;
    }
    
    public Product(int id, String displayImg) {
        this.id = id;
        this.displayImg = displayImg;
    }

    public Product(int id, String title, int sellPrice,
                   String siggName, String displayImg, String status) {
        this.id = id;
        this.title = title;
        this.sellPrice = sellPrice;
        this.siggName = siggName;
        this.displayImg = displayImg;
        this.status = status;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getSellPrice() { return sellPrice; }
    public void setSellPrice(int sellPrice) { this.sellPrice = sellPrice; }

    public String getSiggName() { return siggName; }
    public void setSiggName(String siggName) { this.siggName = siggName; }

    public String getImgName() { return imgName; }
    public void setImgName(String imgName) { this.imgName = imgName; }

    public String getDisplayImg() { return displayImg; }
    public void setDisplayImg(String displayImg) { this.displayImg = displayImg; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getSidoId() { return sidoId; }
    public void setSidoId(int sidoId) { this.sidoId = sidoId; }

    public int getRegionId() { return regionId; }
    public void setRegionId(int regionId) { this.regionId = regionId; }

    public String getImgSrc() { return imgSrc; }
    public void setImgSrc(String imgSrc) { this.imgSrc = imgSrc; }

	public int getSellerId() { return sellerId; }
	public void setSellerId(int sellerId) { this.sellerId = sellerId; }
}
