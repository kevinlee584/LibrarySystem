package com.example.demo.Exception;

public class JwtTokenIsExpired extends Exception{
    public JwtTokenIsExpired(String message) {
        super(message);
    }
}
