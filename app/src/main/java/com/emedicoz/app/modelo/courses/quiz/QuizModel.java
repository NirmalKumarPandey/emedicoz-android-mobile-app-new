package com.emedicoz.app.modelo.courses.quiz;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cbc-03 on 11/01/17.
 */

public class QuizModel implements Serializable {
    private ArrayList<Questions> question_bank;

    private Quiz_Basic_Info basic_info;

    private boolean isResume = false;

    private int quesCount;

    public int getQuesCount() {
        return quesCount;
    }

    public void setQuesCount(int quesCount) {
        this.quesCount = quesCount;
    }

    public boolean isResume() {
        return isResume;
    }

    public void setResume(boolean resume) {
        isResume = resume;
    }

    public ArrayList<Questions> getQuestion_bank() {
        return question_bank;
    }

    public void setQuestion_bank(ArrayList<Questions> question_bank) {
        this.question_bank = question_bank;
    }

    public Quiz_Basic_Info getBasic_info() {
        return basic_info;
    }

    public void setBasic_info(Quiz_Basic_Info basic_info) {
        this.basic_info = basic_info;
    }

}