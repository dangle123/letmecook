package com.example.letmecook.Model;

public class LoaiMonAn {
    private String id;
    private String ten;
    private String url;

    public LoaiMonAn() {
        // Bắt buộc cho Firebase
    }

    public LoaiMonAn(String id, String ten, String url) {
        this.id = id;
        this.ten = ten;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
