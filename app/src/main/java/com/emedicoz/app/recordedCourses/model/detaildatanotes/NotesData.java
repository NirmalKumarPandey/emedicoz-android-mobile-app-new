package com.emedicoz.app.recordedCourses.model.detaildatanotes;

import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotesData implements Serializable {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName(Constants.ResultExtras.TEST_SERIES_NAME)
    @Expose
    private String testSeriesName;
    @SerializedName("time_in_mins")
    @Expose
    private String timeInMins;
    @SerializedName("total_questions")
    @Expose
    private String totalQuestions;
    @SerializedName("is_locked")
    @Expose
    private String isLocked;
    @SerializedName("is_paused")
    @Expose
    private String isPaused;
    @SerializedName("display_reattempt")
    @Expose
    private String display_reattempt;
    @SerializedName("display_review_answer")
    @Expose
    private String display_review_answer;
    @SerializedName("seg_id")
    @Expose
    private String segId;
    @SerializedName("test_start_date")
    @Expose
    private String testStartDate;
    @SerializedName("test_result_date")
    @Expose
    private String testResultDate;
    @SerializedName("test_end_date")
    @Expose
    private String testEndDate;
    @SerializedName(Constants.Extras.TYPE)
    @Expose
    private String type;
    @SerializedName("key_data")
    @Expose
    private String keyData;
    @SerializedName("main_id")
    @Expose
    private String mainId;
    @SerializedName("sub_id")
    @Expose
    private String subId;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("enc_url")
    @Expose
    private String encUrl;
    @SerializedName("enc_file_url")
    @Expose
    private String encFileUrl;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("topic_id")
    @Expose
    private String topicId;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("page_count")
    @Expose
    private String pageCount;
    @SerializedName("backend_user_id")
    @Expose
    private String backendUserId;
    @SerializedName("view_limit")
    @Expose
    private String viewLimit;
    @SerializedName("is_vod")
    @Expose
    private String isVod;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("duration_limit")
    @Expose
    private String durationLimit;
    @SerializedName("ext_duration")
    @Expose
    private String extDuration;
    @SerializedName("users_to_extend")
    @Expose
    private String usersToExtend;
    @SerializedName("channel_url_type")
    @Expose
    private String channelUrlType;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("live_status")
    @Expose
    private String liveStatus;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("instructor")
    @Expose
    private String instructor;
    @SerializedName("creation")
    @Expose
    private String creation;
    @Expose
    private String course_id;
    @Expose
    private String module_id;

    public String getDisplay_reattempt() {
        return display_reattempt;
    }

    public void setDisplay_reattempt(String display_reattempt) {
        this.display_reattempt = display_reattempt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestSeriesName() {
        return testSeriesName;
    }

    public void setTestSeriesName(String testSeriesName) {
        this.testSeriesName = testSeriesName;
    }

    public String getTimeInMins() {
        return timeInMins;
    }

    public void setTimeInMins(String timeInMins) {
        this.timeInMins = timeInMins;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(String isPaused) {
        this.isPaused = isPaused;
    }

    public String getSegId() {
        return segId;
    }

    public void setSegId(String segId) {
        this.segId = segId;
    }

    public String getTestStartDate() {
        return !GenericUtils.isEmpty(testStartDate) ? testStartDate : "0";
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestEndDate() {
        return !GenericUtils.isEmpty(testEndDate) ? testEndDate : "0";
    }

    public void setTestEndDate(String testEndDate) {
        this.testEndDate = testEndDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyData() {
        return keyData;
    }

    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(String encUrl) {
        this.encUrl = encUrl;
    }

    public String getEncFileUrl() {
        return encFileUrl;
    }

    public void setEncFileUrl(String encFileUrl) {
        this.encFileUrl = encFileUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getBackendUserId() {
        return backendUserId;
    }

    public void setBackendUserId(String backendUserId) {
        this.backendUserId = backendUserId;
    }

    public String getViewLimit() {
        return viewLimit;
    }

    public void setViewLimit(String viewLimit) {
        this.viewLimit = viewLimit;
    }

    public String getIsVod() {
        return isVod;
    }

    public void setIsVod(String isVod) {
        this.isVod = isVod;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationLimit() {
        return durationLimit;
    }

    public void setDurationLimit(String durationLimit) {
        this.durationLimit = durationLimit;
    }

    public String getExtDuration() {
        return extDuration;
    }

    public void setExtDuration(String extDuration) {
        this.extDuration = extDuration;
    }

    public String getUsersToExtend() {
        return usersToExtend;
    }

    public void setUsersToExtend(String usersToExtend) {
        this.usersToExtend = usersToExtend;
    }

    public String getChannelUrlType() {
        return channelUrlType;
    }

    public void setChannelUrlType(String channelUrlType) {
        this.channelUrlType = channelUrlType;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getTestResultDate() {
        return testResultDate;
    }

    public void setTestResultDate(String testResultDate) {
        this.testResultDate = testResultDate;
    }

    public String getDisplay_review_answer() {
        return display_review_answer;
    }

    public void setDisplay_review_answer(String display_review_answer) {
        this.display_review_answer = display_review_answer;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

}
