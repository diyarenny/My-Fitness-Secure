package com.example.myfitnesssecure;

public class MyReminders {

    String title;
    String date;
    String desc;
    String key;

    public MyReminders() {
    }

    public MyReminders(String title, String date, String desc, String key) {
        this.title = title;
        this.date = date;
        this.desc = desc;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}