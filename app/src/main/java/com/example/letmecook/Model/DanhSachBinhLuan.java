package com.example.letmecook.Model;

public class DanhSachBinhLuan {
    private String userId;

    private String nameUser;
    private String comment;
    private String timestamp;
    private String avtUser;

    public DanhSachBinhLuan() {}

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public DanhSachBinhLuan(String userId, String nameUser , String comment, String timestamp, String avtUser) {
        this.userId = userId;
        this.nameUser = nameUser;
        this.comment = comment;
        this.timestamp = timestamp;
        this.avtUser = avtUser;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }


    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAvtUser() { return avtUser; }
    public void setAvtUser(String avtUser) { this.avtUser = avtUser; }
}
