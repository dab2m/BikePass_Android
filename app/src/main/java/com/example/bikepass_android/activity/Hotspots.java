package com.example.bikepass_android.activity;

public class Hotspots {

    private double radius;
    private String point_name;
    private int frequency;
    private double latitude;
    private double longitude;

    public Hotspots(double radius, String point_name, int frequency, double latitude, double longitude) {
        this.radius = radius;
        this.point_name = point_name;
        this.frequency = frequency;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(int status_code) {
        this.radius = radius;
    }

    public String getPoint_name() {
        return this.point_name;
    }

    public void setPoint_name(String status_name) {
        this.point_name = point_name;
    }

    public int getfrequency() {
        return this.frequency;
    }

    public void setfrequency(int logo_name) {
        this.frequency = frequency;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
