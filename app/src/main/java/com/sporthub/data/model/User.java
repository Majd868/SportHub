package com.sporthub.data.model;

import java.util.Date;

public class User {
    private String userId;
    private String email;
    private String name;
    private String photoUrl;
    private boolean isPremium;
    private Date premiumExpiryDate;
    private boolean isSeller;
    private double weight;
    private double height;
    private String fitnessGoal;
    private Date createdAt;
    
    public User() {
        // Required empty constructor for Firebase
    }
    
    public User(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.createdAt = new Date();
        this.isPremium = false;
        this.isSeller = false;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
    
    public Date getPremiumExpiryDate() { return premiumExpiryDate; }
    public void setPremiumExpiryDate(Date premiumExpiryDate) { this.premiumExpiryDate = premiumExpiryDate; }
    
    public boolean isSeller() { return isSeller; }
    public void setSeller(boolean seller) { isSeller = seller; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    
    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
