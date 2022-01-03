package com.emedicoz.app.modelo.courses;

public class TestQuizPojo {
    private TestSeries[] test_series;

    private Masters[] masters;

    private Basic basic_info;

    private String is_purchased;

    public TestSeries[] getTest_series() {
        return test_series;
    }

    public void setTest_series(TestSeries[] test_series) {
        this.test_series = test_series;
    }

    public Masters[] getMasters() {
        return masters;
    }

    public void setMasters(Masters[] masters) {
        this.masters = masters;
    }

    public Basic getBasic_info() {
        return basic_info;
    }

    public void setBasic_info(Basic basic_info) {
        this.basic_info = basic_info;
    }

    public String getIs_purchased() {
        return is_purchased;
    }

    public void setIs_purchased(String is_purchased) {
        this.is_purchased = is_purchased;
    }

    @Override
    public String toString() {
        return "ClassPojo [test_series = " + test_series + ", masters = " + masters + "]";
    }
}
			
			