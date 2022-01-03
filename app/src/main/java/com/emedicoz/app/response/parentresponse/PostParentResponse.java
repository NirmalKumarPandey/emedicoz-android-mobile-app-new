package com.emedicoz.app.response.parentresponse;

import com.emedicoz.app.modelo.OwnerInfo;
import com.emedicoz.app.modelo.People;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cbc-03 on 05/22/17.
 */

public class PostParentResponse extends ParentResponse implements Serializable {
    OwnerInfo post_owner_info;
    ArrayList<People> tagged_people;
    private String id;
    private String share;
    private String report_abuse;
    private String post_tag;
    private String pinned_post;
    private String post_tag_id;
    private String name;
    private String thumbnail;
    private String hlslink;
    private String chat_node;
    private String chat_platform;
    private String is_vod;
    private String parent_id;
    private String likes;
    private String creation_time;
    private String post_type;
    private String user_id;
    private String comments;
    private boolean is_liked;
    private boolean is_bookmarked;
    private String is_shared;

    public String getPinned_post() {
        return pinned_post;
    }

    public void setPinned_post(String pinned_post) {
        this.pinned_post = pinned_post;
    }

    public String getPost_tag_id() {
        return post_tag_id;
    }

    public void setPost_tag_id(String post_tag_id) {
        this.post_tag_id = post_tag_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHlslink() {
        return hlslink;
    }

    public void setHlslink(String hlslink) {
        this.hlslink = hlslink;
    }

    public String getChat_node() {
        return chat_node;
    }

    public void setChat_node(String chat_node) {
        this.chat_node = chat_node;
    }

    public String getChat_platform() {
        return chat_platform;
    }

    public void setChat_platform(String chat_platform) {
        this.chat_platform = chat_platform;
    }

    public String getIs_vod() {
        return is_vod;
    }

    public void setIs_vod(String is_vod) {
        this.is_vod = is_vod;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getPost_tag() {
        return post_tag;
    }

    public void setPost_tag(String post_tag) {
        this.post_tag = post_tag;
    }

    public ArrayList<People> getTagged_people() {
        return tagged_people;
    }

    public void setTagged_people(ArrayList<People> tagged_people) {
        this.tagged_people = tagged_people;
    }

    public boolean is_bookmarked() {
        return is_bookmarked;
    }

    public void setIs_bookmarked(boolean is_bookmarked) {
        this.is_bookmarked = is_bookmarked;
    }

    public String getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(String is_shared) {
        this.is_shared = is_shared;
    }

    public boolean isLiked() {
        return is_liked;
    }

    public void setLiked(boolean liked) {
        is_liked = liked;
    }

    public OwnerInfo getPost_owner_info() {
        return post_owner_info;
    }

    public void setPost_owner_info(OwnerInfo post_owner_info) {
        this.post_owner_info = post_owner_info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getReport_abuse() {
        return report_abuse;
    }

    public void setReport_abuse(String report_abuse) {
        this.report_abuse = report_abuse;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public class PostSharedOf implements Serializable {
        private String id;

        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
