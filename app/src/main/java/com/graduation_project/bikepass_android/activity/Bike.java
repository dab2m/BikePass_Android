package com.graduation_project.bikepass_android.activity;



public class Bike {

    private int status_code;
    private String status_name;
    private int  logo_name;
    private int id;
    private double latitude;
    private double longitude;
    private String address;

    public Bike(int status_code,String status_name,int logo_name,int id,double latitude,double longitude,String address){
        this.status_code=status_code;
        this.status_name=status_name;
        this.logo_name=logo_name;
        this.id=id;
        this.latitude=latitude;
        this.longitude=longitude;
        this.address=address;

    }

    public int getStatus_code(){return this.status_code;}

    public void setStatus_code(int status_code){this.status_code=status_code;}

    public String getStatus_name(){return this.status_name;}

    public void setStatus_name(String status_name){this.status_name=status_name;}

    public int getLogo_name(){return this.logo_name;}

    public void setLogo_name(int logo_name){this.logo_name=logo_name;}

    public double getLatitude(){return this.latitude;}

    public void setLatitude(double latitude){this.latitude=latitude;}

    public double getLongitude(){return this.longitude;}

    public void setLongitude(double longitude){this.longitude=longitude;}

    public int getId(){return this.id;}

    public String getAddress(){return this.address;}

    public void setAddress(String address){this.address=address;}

    public String toString(){
        return "id: "+this.getId()+" status code: "+this.getStatus_code()+" status name: "+this.getStatus_name()+" logo name: "+this.getLogo_name()+" latitude: "+this.getLatitude()+" longitude:"+this.getLongitude();
    }
}
