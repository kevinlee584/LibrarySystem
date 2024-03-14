package com.example.demo.models;

import java.sql.Timestamp;

public class Record {
    private Integer inventoryId;
    private String bookName;
    private String author;
    private String status;
    private Timestamp borrowingTime;
    private Timestamp returnTime;

    public Record(Integer inventoryId, String bookName, String author, Timestamp borrowingTime, Timestamp returnTime, String status) {
        this.inventoryId = inventoryId;
        this.bookName = bookName;
        this.author = author;
        this.borrowingTime = borrowingTime;
        this.returnTime = returnTime;
        this.status = status;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getBorrowingTime() {
        return borrowingTime;
    }

    public void setBorrowingTime(Timestamp borrowingTime) {
        this.borrowingTime = borrowingTime;
    }

    public Timestamp getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Timestamp returnTime) {
        this.returnTime = returnTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Record{" +
                "inventoryId=" + inventoryId +
                ", bookName='" + bookName + '\'' +
                ", Author='" + author + '\'' +
                ", borrowingTime=" + borrowingTime +
                ", returnTime=" + returnTime +
                '}';
    }
}
