package com.example.demo.common;

import java.util.List;

public class FormatChecker {

    private static List<String> statuses = List.of("ALLOWED", "BORROWED", "BUSY", "LOST", "DAMAGED", "ABANDONED");
    public static boolean phoneNumberChecker(String phoneNumber){
        return phoneNumber.matches("^\\d{10}$");
    }

    public static boolean isbnChecker(String isbn){
        return isbn.matches("^\\d{3}-\\d-\\d{4}-\\d{4}-\\d$") || isbn.matches("^\\d-\\d{4}-\\d{4}-\\d{1}$");
    }

    public static boolean inventoryIdChecker(Integer inventoryId){
        return String.valueOf(inventoryId).matches("^\\d{0,50}$");
    }

    public static boolean bookStatusChecker(String status){
        return statuses.contains(status);
    }

    public static boolean userNameChecker(String name){
        return name.matches("^\\w{0,50}$");
    }

    public static boolean bookIntroductionChecker(String introduction){
        return introduction.matches("^\\w{0,1000}$");
    }
}
