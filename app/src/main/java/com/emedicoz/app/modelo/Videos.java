package com.emedicoz.app.modelo;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Videos {

    @SerializedName("course_id")
    private String mCourseId;
    @SerializedName("subject_id")
    private String mSubjectId;
    @SerializedName("video_id")
    private String mVideoId;
    @SerializedName("video_url")
    private String mVideoUrl;

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        mCourseId = courseId;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        mVideoUrl = videoUrl;
    }

}
