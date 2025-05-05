package com.example.letmecook.Model;

public class Message {
    private String sender;
    private String text;

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getDatime() {
        return datime;
    }

    private String datime ;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDatime(String datime) {
        this.datime = datime;
    }

    public Message(){}

    public Message(String sender, String text, String datime) {
        this.datime = datime;
        this.text = text;
        this.sender = sender;
    }

}
