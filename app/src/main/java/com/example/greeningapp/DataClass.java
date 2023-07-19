package com.example.greeningapp;

public class DataClass {

    public DataClass(String imageURL, String caption, float ratingbar) {
        this.imageURL = imageURL;
        this.caption = caption;
        this.ratingbar = ratingbar;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public float getRatingbar() {
        return ratingbar;
    }
    public void setRatingbar(float ratingbar) {
        this.ratingbar = ratingbar;
    }


    private String imageURL;
    private String caption;
    private float ratingbar;

    public DataClass(){

    }
}
