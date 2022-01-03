package com.emedicoz.app.installment.model;

import java.io.Serializable;

public class PaymentMeta implements Serializable {
    private String isOpen;

    private String count;

    private String isSelected;

    private String name;

    private AmountDescription amount_description;

    private String id;

    private String cycle;

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AmountDescription getAmount_description() {
        return amount_description;
    }

    public void setAmount_description(AmountDescription amount_description) {
        this.amount_description = amount_description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    @Override
    public String toString() {
        return "ClassPojo [isOpen = " + isOpen + ", count = " + count + ", isSelected = " + isSelected + ", name = " + name + ", amount_description = " + amount_description + ", id = " + id + ", cycle = " + cycle + "]";
    }
}