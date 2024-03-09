package com.example.demo.controllers;

import com.example.demo.models.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class bookController {

    @PostMapping("/add")
    public ResponseEntity<?> addBook(Book book) {
        return null;
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeBook(Book book) {
        return null;
    }

    @PutMapping("/borrow")
    public ResponseEntity<?> borrowBook(Book book) {
        return null;
    }

    @PutMapping("/return")
    public ResponseEntity<?> returnBook(Book book) {
        return null;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBooks(){return null;}
}
