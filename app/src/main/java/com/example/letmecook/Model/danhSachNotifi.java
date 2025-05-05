package com.example.letmecook.Model;

public class danhSachNotifi {

    public String title;
    public String type;
    public String status;
    public String decrible;

    public String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDecrible() {
        return decrible;
    }

    public void setDecrible(String decrible) {
        this.decrible = decrible;
    }

    public void danhSachNotifi() {}

    public danhSachNotifi(String title, String type, String decrible,String note,String status){
        this.title = title;
        this.type = type;
        this.decrible = decrible;
        this.note = note ;
        this.status = status;
    }
}
