package com.final_project.bikepass_android.activity;

/**
 * Created by Dilan on 10.03.2020
 */
 class UsageData {

    private int usage;
    private String user_name;

    public UsageData(){
        usage=0;
        user_name="";

    }

    public UsageData(int usage,String user_name){

        this.usage=usage;
        this.user_name=user_name;
    }

    public void setUsage(int usage){
        this.usage=usage;
    }
    public void setUserName(String user_name){

        this.user_name=user_name;
    }
    public int getUsage(){
        return this.usage;
    }
    public String getUserName(){
        return this.user_name;
    }
}