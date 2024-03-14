package com.example.demo.common;

public class Message {
    private MessageStatus status;
    private String message;

    public Message(MessageStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return message + "|" + message;
    }
}
