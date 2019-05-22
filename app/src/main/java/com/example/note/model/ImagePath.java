package com.example.note.model;

import io.realm.RealmObject;

public class ImagePath extends RealmObject {
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
