package model;

public class Product {
    private int id;             // 상품 ID
    private String title;       // 상품명
    private int sellPrice;      // 판매가
    private String siggName;    // 시군구 이름
    private String imgName;     // 원본 이미지 파일명
    private String displayImg;  // 화면 표시용 이미지 경로
    private int viewCount;      // 조회수
    private String status;      // 상품 상태 (SALE / RESERVED / SOLD_OUT)
    private String description;   // 상품 설명
    private int categoryId;       // 카테고리 ID
    private int sidoId;      // 시/도 ID
    private int regionId;    // 시/군/구 ID
    private String imgSrc;   // 이미지 경로 (대표 이미지 표시용)
    private int sellerId;
    
    // ✅ 상세 조회용 생성자 (DAO의 getProductById에서 사용)
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

    // ✅ 목록/검색용 생성자 (DAO의 listProducts, searchProducts에서 사용)
    public Product(int id, String title, int sellPrice,
                   String siggName, String displayImg, String status) {
        this.id = id;
        this.title = title;
        this.sellPrice = sellPrice;
        this.siggName = siggName;
        this.displayImg = displayImg;
        this.status = status;
    }

    // ✅ 기본 생성자 (MyBatis, JSP Bean 등에서 사용)
    public Product() {}

    // ✅ 게터/세터
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
