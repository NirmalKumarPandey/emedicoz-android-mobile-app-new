package com.emedicoz.app.modelo;

/**
 * Created by Cbc-03 on 06/28/17.
 */

public class Notification {


    private Integer notification_code;
    private String message;
    private String image;
    private String time;
    private NotificationData data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Integer getNotificationCode() {
        return notification_code;
    }

    public void setNotificationCode(Integer notification_code) {
        this.notification_code = notification_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public NotificationData getNotificationData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }


    public class NotificationData {


        private String post_id;
        private String user_id;
        private String comment_id;
        private String message_target;
        private String url;

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

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getMessage_target() {
            return message_target;
        }

        public void setMessage_target(String message_target) {
            this.message_target = message_target;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

}