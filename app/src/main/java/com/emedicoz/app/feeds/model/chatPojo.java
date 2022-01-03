package com.emedicoz.app.feeds.model;

import com.emedicoz.app.modelo.Poll;

import java.io.Serializable;

public class chatPojo implements Serializable {
    String id;
    String message;
    String name;
    String date;
    String is_active;
    String profile_picture;
    String online;
    String type;
    String erp_token;
    private Original original;
    String image;
    String chatType;
    Poll poll;

    public chatPojo() {
    }


    public chatPojo(String id, String message, String name, String date, String is_active, String profile_picture, String type, String erp_token,String image) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.date = date;
        this.is_active = is_active;
        this.profile_picture = profile_picture;
        this.online = online;
        this.type = type;
        this.erp_token = erp_token;
        this.image = image;
    }

    public chatPojo(String id, String message, String name, String date, String is_active, String profile_picture, String type, String erp_token,String image,String chatType,Poll poll) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.date = date;
        this.is_active = is_active;
        this.profile_picture = profile_picture;
        this.online = online;
        this.type = type;
        this.erp_token = erp_token;
        this.image = image;
        this.chatType = chatType;
        this.poll = poll;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public Original getOriginal() {
        return original;
    }

    public void setOriginal(Original original) {
        this.original = original;
    }

    public String getErp_token() {
        return erp_token;
    }

    public void setErp_token(String erp_token) {
        this.erp_token = erp_token;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    @Override
    public String toString() {
        return "chatPojo{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", is_active='" + is_active + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", online='" + online + '\'' +
                ", type='" + type + '\'' +
                ", erp_token='" + erp_token + '\'' +
                ", original=" + original +
                ", image='" + image + '\'' +
                ", chatType='" + chatType + '\'' +
                ", poll=" + poll +
                '}';
    }
}
