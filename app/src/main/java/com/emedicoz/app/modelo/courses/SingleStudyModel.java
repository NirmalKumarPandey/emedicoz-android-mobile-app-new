package com.emedicoz.app.modelo.courses;

import java.io.Serializable;
import java.util.ArrayList;

public class SingleStudyModel implements Serializable {

    private ArrayList<Cards> tiles;

    private Basic basic;

    private String is_purchased;

    private Instructor instructor_data;

    private String gst;

    private String points_conversion_rate;

    public String getIs_purchased() {
        return is_purchased;
    }

    public void setIs_purchased(String is_purchased) {
        this.is_purchased = is_purchased;
    }

    public ArrayList<Cards> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Cards> tiles) {
        this.tiles = tiles;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public Instructor getInstructor_data() {
        return instructor_data;
    }

    public void setInstructor_data(Instructor instructor_data) {
        this.instructor_data = instructor_data;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getPoints_conversion_rate() {
        return points_conversion_rate;
    }

    public void setPoints_conversion_rate(String points_conversion_rate) {
        this.points_conversion_rate = points_conversion_rate;
    }
}
