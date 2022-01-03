package com.emedicoz.app.installment.model;

public class Plan {

    boolean isSelected = false;

    public Plan(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
