package com.emedicoz.app.modelo;


import com.emedicoz.app.utilso.GenericUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cbc-03 on 06/02/17.
 */

public class Comment implements Serializable {
    private String id;

    private String profile_picture;
    private String parent_id;
    private String sub_comment_count;
    private String time;
    private String likes;
    private String name;
    private String is_expert;
    private String image;
    private String user_id;
    private String comment;
    private String is_like;
    private String post_id;
    private ArrayList<People> tagged_people;

    public String getIs_expert() {
        return is_expert;
    }

    public void setIs_expert(String is_expert) {
        this.is_expert = is_expert;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getSub_comment_count() {
        return sub_comment_count;
    }

    public void setSub_comment_count(String sub_comment_count) {
        this.sub_comment_count = sub_comment_count;
    }

    public ArrayList<People> getTagged_people() {
        return tagged_people;
    }

    public void setTagged_people(ArrayList<People> tagged_people) {
        this.tagged_people = tagged_people;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getLikes() {
        return GenericUtils.isEmpty(likes) ? "0" : likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }
}
