package com.example.demo.models;

public class Review {
    private String userName;
    private String comment;
    private Integer rate;

    public Review(String userName, String comment, Integer rate) {
        this.userName = userName;
        this.comment = comment;
        this.rate = rate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}
