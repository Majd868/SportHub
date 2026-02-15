package com.sporthub.data.model;

import java.util.Date;

public class SocialPost {
    private String postId;
    private String userId;
    private String userName;
    private String userPhoto;
    private String content;
    private String imageUrl;
    private int likes;
    private int comments;
    private Date createdAt;
    
    public SocialPost() {
        this.createdAt = new Date();
        this.likes = 0;
        this.comments = 0;
    }
    
    public SocialPost(String userId, String userName, String content) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.content = content;
    }
    
    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserPhoto() { return userPhoto; }
    public void setUserPhoto(String userPhoto) { this.userPhoto = userPhoto; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    
    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
