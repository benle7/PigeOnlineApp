package com.example.pigeonlineandroidapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.pigeonlineandroidapp.R;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat {

    @PrimaryKey(autoGenerate=true)
    private int id;

    private String chatWith;
    private String lastMessage;
    private String date;
    private String displayName;
    private String image;
    private String serverURL;
    private String chatOwner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatWith() {
        return chatWith;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getChatOwner() {
        return chatOwner;
    }

    public void setChatOwner(String chatOwner) {
        this.chatOwner = chatOwner;
    }

    public Chat(String chatWith, String displayName, String serverURL, String chatOwner) {
        this.chatWith = chatWith;
        this.lastMessage = "";
        this.date = "";
        this.displayName = displayName;
        this.serverURL = serverURL;
        this.chatOwner = chatOwner;
    }

    public Chat() {
    }
}
