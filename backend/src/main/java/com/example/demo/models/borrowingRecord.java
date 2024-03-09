package com.example.demo.models;

import java.sql.Date;

public class borrowingRecord {
    private Integer userId;
    private Integer inventoryId;
    private Date borrowingTime;
    private Date returnTime;

    public borrowingRecord(Integer userId, Integer inventoryId, Date borrowingTime, Date returnTime) {
        this.userId = userId;
        this.inventoryId = inventoryId;
        this.borrowingTime = borrowingTime;
        this.returnTime = returnTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Date getBorrowingTime() {
        return borrowingTime;
    }

    public void setBorrowingTime(Date borrowingTime) {
        this.borrowingTime = borrowingTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    @Override
    public String toString() {
        return "borrowingRecord{" +
                "userId=" + userId +
                ", inventoryId=" + inventoryId +
                ", borrowingTime=" + borrowingTime +
                ", returnTime=" + returnTime +
                '}';
    }
}
