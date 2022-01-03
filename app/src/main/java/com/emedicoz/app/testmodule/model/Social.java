package com.emedicoz.app.testmodule.model;

public class Social {

    public String name;
    public String option;
    public int tag;
    public String selcted = "-1";
    public boolean parent = false;
    // flag when item swiped
    public boolean swiped = false;
    private boolean isChecked = false;

    public Social() {
    }

    public Social(String option, String name, int tag) {
        this.option = option;
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getSelcted() {
        return selcted;
    }

    public void setSelcted(String selcted) {
        this.selcted = selcted;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public boolean isSwiped() {
        return swiped;
    }

    public void setSwiped(boolean swiped) {
        this.swiped = swiped;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
