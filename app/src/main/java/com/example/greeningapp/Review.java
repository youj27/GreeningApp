package com.example.greeningapp;

public class Review {
    private String rimage;
    private String rcontent;
    private float rscore;

    private String pname;
    private int pid;
    private  String pimg;
    private String rdatetime;
    private String username;

    private String idToken;

    private String pprice;

    private int totalquantity;

    private String reviewid;

    private double similarity;

    public Review() {

    }



    public String getRimage() {
        return rimage;
    }

    public void setRimage(String rimage) {
        this.rimage = rimage;
    }

    public String getRcontent() {
        return rcontent;
    }

    public void setRcontent(String rcontent) {
        this.rcontent = rcontent;
    }

    public float getRscore() {
        return rscore;
    }

    public void setRscore(float rscore) {
        this.rscore = rscore;
    }

    public String getRdatetime() {
        return rdatetime;
    }

    public void setRdatetime(String rdatetime) {
        this.rdatetime = rdatetime;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPimg() {
        return pimg;
    }

    public void setPimg(String pimg) {
        this.pimg = pimg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPprice() { return pprice; }
    public void setPprice(String pprice) { this.pprice = pprice; }

    public int getTotalquantity() {
        return totalquantity;
    }

    public void setTotalquantity(int totalquantity) {
        this.totalquantity = totalquantity;
    }

    public String getReviewid() {
        return reviewid;
    }

    public void setReviewid(String reviewid) {
        this.reviewid = reviewid;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    // 추가: 유사도 설정 메서드
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
