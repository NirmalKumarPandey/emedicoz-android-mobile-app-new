package com.emedicoz.app.modelo;

import java.io.Serializable;

public class RewardTransaction implements Serializable {
    private String id;

    private String area;

    private String reward;

    private String creation_time;

    private String user_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", area = " + area + ", reward = " + reward + ", creation_time = " + creation_time + ", user_id = " + user_id + "]";
    }
}
