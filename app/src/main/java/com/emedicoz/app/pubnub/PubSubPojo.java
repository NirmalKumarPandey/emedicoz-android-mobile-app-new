package com.emedicoz.app.pubnub;


import com.emedicoz.app.utilso.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.common.internal.Objects;

public class PubSubPojo {


    private String sender;
    private String message;
    private String timestamp;
    private String type;
    private String id;
    private String profile_pic;

    public PubSubPojo(@JsonProperty(Constants.Extras.ID) String id, @JsonProperty("sender") String sender, @JsonProperty("message") String message, @JsonProperty("timestamp") String timestamp, @JsonProperty(Constants.Extras.TYPE) String type, @JsonProperty("profile_pic") String profile_pic) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.profile_pic = profile_pic;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PubSubPojo other = (PubSubPojo) obj;

        return Objects.equal(this.id, other.id)
                && Objects.equal(this.sender, other.sender)
                && Objects.equal(this.message, other.message)
                && Objects.equal(this.timestamp, other.timestamp)
                && Objects.equal(this.type, other.type)
                && Objects.equal(this.profile_pic, other.profile_pic);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, sender, message, timestamp, type, profile_pic);
    }

/*    public static PubSubPojo serialize(PNHistoryItemResult pnHistoryItemResult) {
        PubSubPojo message = new Gson().fromJson(pnHistoryItemResult.getEntry(), PubSubPojo.class);
        return message;
    }

    public static PubSubPojo serialize(PNMessageResult pnMessageResult) {
        PubSubPojo message = new Gson().fromJson(pnMessageResult.getMessage(), PubSubPojo.class);
        return message;
    }*/
}
