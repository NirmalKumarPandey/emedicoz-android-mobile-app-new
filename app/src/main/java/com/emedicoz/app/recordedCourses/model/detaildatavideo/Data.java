package com.emedicoz.app.recordedCourses.model.detaildatavideo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {

    @SerializedName("course_total_data")
    private CourseTotalData courseTotalData;
    @SerializedName("topic_video_data")
    private List<TopicVideoDatum> topicVideoData;

    public CourseTotalData getCourseTotalData() {
        return courseTotalData;
    }

    public void setCourseTotalData(CourseTotalData courseTotalData) {
        this.courseTotalData = courseTotalData;
    }

    public List<TopicVideoDatum> getTopicVideoData() {
        return topicVideoData;
    }

    public void setTopicVideoData(List<TopicVideoDatum> topicVideoData) {
        this.topicVideoData = topicVideoData;
    }

}
