package com.example.greeningapp;

public class User {
    private String profile;
    private String pname;
    private String product;
    private int cost;

    public User(){

    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getCost() { return cost; }
    public void setCost(int cost) {
        this.cost = cost;
    }
}
