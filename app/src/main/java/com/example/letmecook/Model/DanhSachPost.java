package com.example.letmecook.Model;

import java.util.ArrayList;
import java.util.List;

public class DanhSachPost {

    private String userId;
    private String content;
    private String timestamp;
    private String imageUrl;
    private String imageUser;
    private List<String> likes;
    private List<String> comments;
    private  String userName;
    private String postId;



    public DanhSachPost() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public DanhSachPost(String userId, String userName, String content, String timestamp, String imageUrl, String imageUser, String postId) {
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.imageUser = imageUser;
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.userName =  userName;
        this.postId = postId;
    }


    public String getUserId() {
        return userId;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUser() { return imageUser;}

    public void setImageUser(String imageUser) { this.imageUser = imageUser;}

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
