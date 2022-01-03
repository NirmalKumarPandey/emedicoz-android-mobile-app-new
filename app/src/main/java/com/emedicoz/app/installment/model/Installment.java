package com.emedicoz.app.installment.model;

import java.io.Serializable;

public class Installment implements Serializable {
    private boolean isSelected = false;
    private boolean isOpen = false;
    private String name;
    private String count;
    private String id;
    private AmountDescription amount_description;
    private String cycle;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AmountDescription getAmount_description() {
        return amount_description;
    }

    public void setAmount_description(AmountDescription amount_description) {
        this.amount_description = amount_description;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    @Override
    public String toString() {
        return "ClassPojo [name = " + name + ", count = " + count + ", id = " + id + ", amount_description = " + amount_description + ", cycle = " + cycle + "]";
    }
}