package com.example.letmecook.Model;

import java.util.ArrayList;
import java.util.List;

public class DanhSachMonAn {

    private String ten;
    private String hinhAnh;
    private String timecook;

    private String video;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTimecook() {
        return timecook;
    }

    public void setTimecook(String timecook) {
        this.timecook = timecook;
    }

    private boolean checkLike;

    private boolean checkLove;

    public boolean isCheckLove() {
        return checkLove;
    }

    public void setCheckLove(boolean checkLove) {
        this.checkLove = checkLove;
    }

    private int viewUser;

    public int getViewUser() {
        return viewUser;
    }

    public void setViewUser(int viewUser) {
        this.viewUser = viewUser;
    }

    public int getLikeUser() {
        return likeUser;
    }

    public void setLikeUser(int likeUser) {
        this.likeUser = likeUser;
    }

    public int getCoinUser() {
        return coinUser;
    }

    public void setCoinUser(int coinUser) {
        this.coinUser = coinUser;
    }

    private int likeUser;

    private int coinUser;
    private  String id ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheckLike() {
        return checkLike;
    }

    public void setCheckLike(boolean checkLike) {
        this.checkLike = checkLike;
    }

    private  String categories_id;
    private ArrayList<String> nguyenlieu;
    protected ArrayList<String> buocnau;
    protected ArrayList<String> tips;

    public void setNguyenlieu(ArrayList<String> nguyenlieu) {
        this.nguyenlieu = nguyenlieu;
    }
    public ArrayList<String> getNguyenlieu() {
        return nguyenlieu;
    }
    public void setBuocnau(ArrayList<String> buocnau) {
        this.buocnau = buocnau;
    }
    public ArrayList<String> getBuocnau() {
        return buocnau;
    }
    public ArrayList<String> getTips() {
        return tips;
    }

    public void setTips(ArrayList<String> tips) {
        this.tips = tips;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getCategories_id() {
        return categories_id;
    }

    public void setCategories_id(String categories_id) {
        this.categories_id = categories_id;
    }



    public String getCategoriesId() {
        return categories_id;
    }


    public DanhSachMonAn() {} // Bắt buộc để Firebase deserialize

    public DanhSachMonAn(String id,String ten, String hinhAnh,String video, String categories_id, ArrayList<String> nguyenlieu,ArrayList<String> buocnau,ArrayList<String> tips, boolean checkLike, int coinUser, int viewUser, int likeUser,String timecook, boolean checkLove ) {
       this.id = (id != null) ? id :"";
        this.ten = (ten != null) ? ten : "";
        this.hinhAnh = (hinhAnh != null) ? hinhAnh : "";
        this.video = (video != null) ? video : "https://res.cloudinary.com/dqbehf1sz/video/upload/v1762085027/t%E1%BA%A3i_xu%E1%BB%91ng_gs3ibr.mp4";
        this.nguyenlieu = (nguyenlieu != null) ? new ArrayList<>(nguyenlieu) : new ArrayList<>();
        this.buocnau = (buocnau != null) ? new ArrayList<>(buocnau) : new ArrayList<>();
        this.categories_id = (categories_id != null) ? categories_id : "";;
        this.checkLike = checkLike;
        this.coinUser = coinUser;
        this.likeUser = likeUser;
        this.viewUser = viewUser;
        this.timecook = timecook;
        this.checkLove = checkLove;
        this.tips = (buocnau != null) ? new ArrayList<>(tips) : new ArrayList<>();
    }



}



