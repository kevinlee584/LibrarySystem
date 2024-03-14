package com.example.demo.common;

public enum MessageStatus {
    success, fail, expired;

    @Override
    public String toString(){
        return switch (this.ordinal()) {
            case 0 -> "success";
            case 1 -> "fail";
            case 2 -> "expired";
            default -> null;
        };
    }
}
