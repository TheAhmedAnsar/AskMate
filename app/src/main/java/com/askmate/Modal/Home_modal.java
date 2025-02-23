package com.askmate.Modal;

import java.util.ArrayList;

public class Home_modal {
//    private String imageurl;
    private ArrayList<String> urls;
    private String classname;
    private String address;

    public Home_modal(ArrayList<String> urls, String classname, String address, String key, String distance) {
        this.urls = urls;
        this.classname = classname;
        this.address = address;
        this.key = key;
        this.distance = distance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;


    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private String distance;

//    public Home_modal(ArrayList<String> urls, String classname, String address, String distance) {
//        this.urls = urls;
//        this.classname = classname;
//        this.address = address;
//        this.distance = distance;
//    }

    public Home_modal() {
    }
}
