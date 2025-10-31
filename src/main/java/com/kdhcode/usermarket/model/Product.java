package com.kdhcode.usermarket.model;

public class Product {
    private int id;
    private String title;
    private int sellPrice;
    private String siggName;
    private String imgName;
    private String displayImg;

    public Product(int id, String title, int sellPrice, String siggName, String imgName) {
        this.id = id;
        this.title = title;
        this.sellPrice = sellPrice;
        this.siggName = siggName;
        this.imgName = imgName;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getSellPrice() { return sellPrice; }
    public String getSiggName() { return siggName; }
    public String getImgName() { return imgName; }
    public String getDisplayImg() { return displayImg; }
    public void setDisplayImg(String displayImg) { this.displayImg = displayImg; }
}
