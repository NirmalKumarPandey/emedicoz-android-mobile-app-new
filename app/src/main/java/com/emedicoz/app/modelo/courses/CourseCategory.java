package com.emedicoz.app.modelo.courses;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 09/04/17.
 */

public class CourseCategory implements Serializable {
    private String position;

    private String id;

    private String name;

    private String app_view_type;

    private String parent_fk;

    private String image;

    private String in_carousel;

    private boolean isSelected = false;

    private String category_tag;

    public String getCategory_tag() {
        return category_tag;
    }

    public void setCategory_tag(String category_tag) {
        this.category_tag = category_tag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIn_carousel() {
        return in_carousel;
    }

    public void setIn_carousel(String in_carousel) {
        this.in_carousel = in_carousel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp_view_type() {
        return app_view_type;
    }

    public void setApp_view_type(String app_view_type) {
        this.app_view_type = app_view_type;
    }

    public String getParent_fk() {
        return parent_fk;
    }

    public void setParent_fk(String parent_fk) {
        this.parent_fk = parent_fk;
    }

}
