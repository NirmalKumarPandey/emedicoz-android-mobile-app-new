package com.emedicoz.app.testmodule.model;

public class TrueFalse {
    private String optionSerial;
    private String optionText;

    public TrueFalse(String optionSerial, String optionText) {
        this.optionSerial = optionSerial;
        this.optionText = optionText;
    }

    public String getOptionSerial() {
        return optionSerial;
    }

    public void setOptionSerial(String optionSerial) {
        this.optionSerial = optionSerial;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
