package com.example.bikepass_android.activity;

 class Bike {

    private int status_code;
    private String status_name;
    private int  logo_name;

    public Bike(int status_code,String status_name,int logo_name){
        this.status_code=status_code;
        this.status_name=status_name;
        this.logo_name=logo_name;
    }

    public int getStatus_code(){return this.status_code;}

    public String getStatus_name(){return this.status_name;}

    public int getLogo_name(){return this.logo_name;}
}
