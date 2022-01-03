package com.emedicoz.app.modelo;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 05/25/17.
 */

public class People implements Serializable {

    String id;
    String profile_picture;
    String name;
    String post_id;
    String user_id;
    String is_follow = "0";
    private String time;
    private String specification;
    private String followers_count;
    private boolean watcher_following;
    private String follower_id;

    public String getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(String followers_count) {
        this.followers_count = followers_count;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isWatcher_following() {
        return watcher_following;
    }

    public void setWatcher_following(boolean watcher_following) {
        this.watcher_following = watcher_following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
