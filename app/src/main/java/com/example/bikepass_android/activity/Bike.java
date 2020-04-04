package com.example.bikepass_android.activity;

 public class Bike {

    private int status_code;
    private String status_name;
    private int  logo_name;
    private int id;

    public Bike(int status_code,String status_name,int logo_name,int id){
        this.status_code=status_code;
        this.status_name=status_name;
        this.logo_name=logo_name;
        this.id=id;
    }

    public int getStatus_code(){return this.status_code;}

    public void setStatus_code(int status_code){this.status_code=status_code;}

    public String getStatus_name(){return this.status_name;}

     public void setStatus_name(String status_name){this.status_name=status_name;}

    public int getLogo_name(){return this.logo_name;}

    public void setLogo_name(int logo_name){this.logo_name=logo_name;}
    
    public int getId(){return this.id;}
}
