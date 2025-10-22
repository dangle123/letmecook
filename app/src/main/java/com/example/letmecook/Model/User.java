package com.example.letmecook.Model;

public class User {
    private String name;
    private String avata;
    private  String userId;
    private String email;
    private Integer coin;
    private String birth;


    public User() {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvata() {
        return avata;
    }

    public void setAvata(String avata) {
        this.avata = avata;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public User(String name, String avata, String email, String birth, Integer coin, String userId){
        this.name = name ;
        this.coin = coin;
        this.avata = avata;
        this.email = email;
        this.birth = birth;
        this.userId = userId;
    }
}
