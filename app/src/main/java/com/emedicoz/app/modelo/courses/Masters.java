package com.emedicoz.app.modelo.courses;

public class Masters {
    private String id;

    private String test_type_title;

    private String attempt;

    private String avg_percentage;

    private String max_mark;

    private String category_for;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest_type_title() {
        return test_type_title;
    }

    public void setTest_type_title(String test_type_title) {
        this.test_type_title = test_type_title;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getAvg_percentage() {
        return avg_percentage;
    }

    public void setAvg_percentage(String avg_percentage) {
        this.avg_percentage = avg_percentage;
    }

    public String getMax_mark() {
        return max_mark;
    }

    public void setMax_mark(String max_mark) {
        this.max_mark = max_mark;
    }

    public String getCategory_for() {
        return category_for;
    }

    public void setCategory_for(String category_for) {
        this.category_for = category_for;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", test_type_title = " + test_type_title + "]";
    }
}
			
			