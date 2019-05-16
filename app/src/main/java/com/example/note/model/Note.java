package com.example.note.model;

import android.graphics.Color;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Note extends RealmObject {
    private int id;
    private String title;
    private String content;
    private int color = Color.parseColor("#FFFFFF");
    private String date;
    private String timeAlarm;
    private String dateCreate;
    private boolean isAlarm = false;
    private RealmList<ImagePath> images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeAlarm() {
        return timeAlarm;
    }

    public void setTimeAlarm(String timeAlarm) {
        this.timeAlarm = timeAlarm;
    }

    public boolean isAlarm() {
        return isAlarm;
    }

    public void setAlarm(boolean alarm) {
        isAlarm = alarm;
    }

    public RealmList<ImagePath> getImages() {
        return images;
    }

    public void setImages(RealmList<ImagePath> images) {
        this.images = images;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }
}
