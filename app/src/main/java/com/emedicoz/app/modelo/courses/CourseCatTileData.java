package com.emedicoz.app.modelo.courses;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CourseCatTileData implements Serializable {
    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("main_cat")
    @Expose
    private String mainCat;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName(Constants.Extras.TYPE)
    @Expose
    private String type;
    @SerializedName("categorytype")
    @Expose
    private String categorytype;
    @SerializedName("identifier_key")
    @Expose
    private String identifier;
    @SerializedName("webcategory")
    @Expose
    private String webcategory;
    @SerializedName("appcategory")
    @Expose
    private String appcategory;
    @SerializedName("course")
    @Expose
    private String course;
    @SerializedName("course_detail")
    @Expose
    private CourseDetail courseDetail;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("image")
    @Expose
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainCat() {
        return mainCat;
    }

    public void setMainCat(String mainCat) {
        this.mainCat = mainCat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategorytype() {
        return categorytype;
    }

    public void setCategorytype(String categorytype) {
        this.categorytype = categorytype;
    }

    public String getWebcategory() {
        return webcategory;
    }

    public void setWebcategory(String webcategory) {
        this.webcategory = webcategory;
    }

    public String getAppcategory() {
        return appcategory;
    }

    public void setAppcategory(String appcategory) {
        this.appcategory = appcategory;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public CourseDetail getCourseDetail() {
        return courseDetail;
    }

    public void setCourseDetail(CourseDetail courseDetail) {
        this.courseDetail = courseDetail;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
