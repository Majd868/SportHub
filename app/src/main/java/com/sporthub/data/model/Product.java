package com.sporthub.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product {
    private String productId;
    private String sellerId;
    private String name;
    private String description;
    private String category;
    private double price;
    private String imageUrl;
    private List<String> images;
    private int stock;
    private double rating;
    private int reviewCount;
    private boolean available;
    private Date createdAt;
    
    public Product() {
        this.images = new ArrayList<>();
        this.available = true;
        this.createdAt = new Date();
        this.rating = 0.0;
        this.reviewCount = 0;
    }
    
    public Product(String name, String description, String category, double price) {
        this();
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }
    
    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
