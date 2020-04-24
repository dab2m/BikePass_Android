package com.final_project.bikepass_android.activity;

/**
 * Created by Berk on 24.04.2020
 */
public class WeeklyData {
    String day;
    int seconds;

    public WeeklyData(String day, int seconds) {
        this.day = day;
        this.seconds = seconds;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}