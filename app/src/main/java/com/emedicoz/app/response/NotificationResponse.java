package com.emedicoz.app.response;

import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.parentresponse.ParentResponse;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 07/13/17.
 */

public class NotificationResponse extends ParentResponse implements Serializable {

    private String id;

    private String creation_time;

    private User action_performed_by;

    private String post_id;
    private String comment_id;
    private String activity_type;
    private int view_state;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public int getView_state() {
        return view_state;
    }

    public void setView_state(int view_state) {
        this.view_state = view_state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public User getAction_performed_by() {
        return action_performed_by;
    }

    public void setAction_performed_by(User action_performed_by) {
        this.action_performed_by = action_performed_by;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }
}
