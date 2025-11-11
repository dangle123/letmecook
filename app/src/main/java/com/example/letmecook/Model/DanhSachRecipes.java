package com.example.letmecook.Model;

import java.util.ArrayList;

public class DanhSachRecipes {
    private String title;
    private String url;
    private String cooking_time;
    private double ratings;
    private int like;
    private String id;

    private Integer coin;

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCooking_time() {
        return cooking_time;
    }

    public void setCooking_time(String cooking_time) {
        this.cooking_time = cooking_time;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    private int view;

    public DanhSachRecipes() {} // cần có constructor rỗng

    public DanhSachRecipes(String id, String title, String url,  int coin, int viewUser, int likeUser, String timecook ) {
        this.id = (id != null) ? id :"";
        this.title = (title != null) ? title : "";
        this.url = (url != null) ? url : "";
        this.coin = coin;
        this.like = likeUser;
        this.view = viewUser;
        this.cooking_time = timecook;
    }
}
