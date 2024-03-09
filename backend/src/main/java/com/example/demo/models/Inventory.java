package com.example.demo.models;

import java.sql.Date;

public class Inventory {
    private Integer id;
    private Integer ISBN;
    private Date storeTime;
    private String statue;

    public Inventory(Integer id, Integer ISBN, Date storeTime, String statue) {
        this.id = id;
        this.ISBN = ISBN;
        this.storeTime = storeTime;
        this.statue = statue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getISBN() {
        return ISBN;
    }

    public void setISBN(Integer ISBN) {
        this.ISBN = ISBN;
    }

    public Date getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(Date storeTime) {
        this.storeTime = storeTime;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", ISBN=" + ISBN +
                ", storeTime=" + storeTime +
                ", statue='" + statue + '\'' +
                '}';
    }
}
