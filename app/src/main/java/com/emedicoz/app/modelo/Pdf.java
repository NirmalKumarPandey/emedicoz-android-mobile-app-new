
package com.emedicoz.app.modelo;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Pdf {

    @SerializedName("course_id")
    private String mCourseId;
    @SerializedName("pdf_id")
    private String mPdfId;
    @SerializedName("pdf_url")
    private String mPdfUrl;
    @SerializedName("subject_id")
    private String mSubjectId;

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        mCourseId = courseId;
    }

    public String getPdfId() {
        return mPdfId;
    }

    public void setPdfId(String pdfId) {
        mPdfId = pdfId;
    }

    public String getPdfUrl() {
        return mPdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        mPdfUrl = pdfUrl;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

}
