package com.emedicoz.app.response;

import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.response.registration.CoursesInterestedResponse;
import com.emedicoz.app.response.registration.SpecializationResponse;
import com.emedicoz.app.response.registration.StreamResponse;
import com.emedicoz.app.response.registration.SubStreamResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sagar on 05-01-2018.
 */

public class MasterRegistrationResponse implements Serializable {


    private ArrayList<CoursesInterestedResponse> intersted_course;

    private ArrayList<SpecializationResponse> specialization;

    private ArrayList<SubStreamResponse> main_sub_category;

    private ArrayList<StreamResponse> main_category;

    private ArrayList<Tags> all_tags;

    public ArrayList<CoursesInterestedResponse> getIntersted_course() {
        return intersted_course;
    }

    public void setIntersted_course(ArrayList<CoursesInterestedResponse> intersted_course) {
        this.intersted_course = intersted_course;
    }

    public ArrayList<Tags> getAll_tags() {
        return all_tags;
    }

    public void setAll_tags(ArrayList<Tags> all_tags) {
        this.all_tags = all_tags;
    }

    public ArrayList<SpecializationResponse> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(ArrayList<SpecializationResponse> specialization) {
        this.specialization = specialization;
    }

    public ArrayList<SubStreamResponse> getMain_sub_category() {
        return main_sub_category;
    }

    public void setMain_sub_category(ArrayList<SubStreamResponse> main_sub_category) {
        this.main_sub_category = main_sub_category;
    }

    public ArrayList<StreamResponse> getMain_category() {
        return main_category;
    }

    public void setMain_category(ArrayList<StreamResponse> main_category) {
        this.main_category = main_category;
    }


    @Override
    public String toString() {
        return "ClassPojo [intersted_course = " + intersted_course + ", specialization = " + specialization + ", main_sub_category = " + main_sub_category + ", main_category = " + main_category + "]";
    }
}
