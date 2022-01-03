
package com.emedicoz.app.modelo;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Epub {

    @SerializedName("course_id")
    private String mCourseId;
    @SerializedName("epub_id")
    private String mEpubId;
    @SerializedName("epub_url")
    private String mEpubUrl;
    @SerializedName("subject_id")
    private String mSubjectId;

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        mCourseId = courseId;
    }

    public String getEpubId() {
        return mEpubId;
    }

    public void setEpubId(String epubId) {
        mEpubId = epubId;
    }

    public String getEpubUrl() {
        return mEpubUrl;
    }

    public void setEpubUrl(String epubUrl) {
        mEpubUrl = epubUrl;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

}
