package com.example.pigeonlineandroidapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {

    @PrimaryKey(autoGenerate=true)
    private int id;

    private String from;
    private String content;
    private String type;
    private String date;
    private String senderPicture;
    private int chatOwnerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderPicture() {
        return senderPicture;
    }

    public void setSenderPicture(String senderPicture) {
        this.senderPicture = senderPicture;
    }

    public int getChatOwnerId() {
        return chatOwnerId;
    }

    public void setChatOwnerId(int chatOwnerId) {
        this.chatOwnerId = chatOwnerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Message() {
    }

    public Message(String from, String content, String date, int chatOwnerId, String type) {
        this.from = from;
        this.content = content;
        this.date = date;
        this.senderPicture = "im3";
        this.chatOwnerId = chatOwnerId;
        this.type = type;
    }
}
