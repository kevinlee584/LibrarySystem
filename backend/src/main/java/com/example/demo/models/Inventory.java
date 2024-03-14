package com.example.demo.models;

import java.sql.Timestamp;

public class Inventory {
    private Integer id;
    private String ISBN;
    private Timestamp storeTime;
    private String statue;

    public Inventory(Integer id, String ISBN, Timestamp storeTime, String statue) {
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

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public Timestamp getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(Timestamp storeTime) {
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
