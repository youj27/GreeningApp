package com.example.greeningapp;

public class DataClass {

    public DataClass(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;

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


    private String imageURL;
    private String caption;


    public DataClass(){

    }
}
