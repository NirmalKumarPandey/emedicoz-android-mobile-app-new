package com.emedicoz.app.modelo;

import java.io.Serializable;

public class MyRewardPoints implements Serializable {
    private String id;

    private String reward_points;
    private String refer_code;

    public String getRefer_code() {
        return refer_code;
    }

    public void setRefer_code(String refer_code) {
        this.refer_code = refer_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReward_points() {
        return reward_points;
    }

    public void setReward_points(String reward_points) {
        this.reward_points = reward_points;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", reward_points = " + reward_points + "]";
    }
}