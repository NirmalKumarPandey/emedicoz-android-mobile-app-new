package com.emedicoz.app.response;

public class SubscriptionOptionList {

    private String id;
    private String course_id;
    private String months;
    private String mrp;
    private String for_dams;
    private String non_dams;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return course_id;
    }

    public void setCourseId(String courseId) {
        this.course_id = courseId;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getForDams() {
        return for_dams;
    }

    public void setForDams(String forDams) {
        this.for_dams = forDams;
    }

    public String getNonDams() {
        return non_dams;
    }

    public void setNonDams(String nonDams) {
        this.non_dams = nonDams;
    }
}
