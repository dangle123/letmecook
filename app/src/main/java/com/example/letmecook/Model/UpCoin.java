package com.example.letmecook.Model;

public class UpCoin {
    String value;
    String coin;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public UpCoin(){}

    public UpCoin(String value, String coin){
        this.value = value ;
        this.coin = coin;
    }
}
