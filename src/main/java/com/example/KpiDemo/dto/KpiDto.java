package com.example.KpiDemo.dto;

public class KpiDto {

    private int be_val;
    private int now_val;
    private double target_rate;

    public int getBe_val() {
        return be_val;
    }
    public void setBe_val(int be_val) {
        this.be_val = be_val;
    }

    public int getNow_val() {
        return now_val;
    }
    public void setNow_val(int now_val) {
        this.now_val = now_val;
    }

    public double getTarget_rate() {
        return target_rate;
    }
    public void setTarget_rate(double target_rate) {
        this.target_rate = target_rate;
    }
}