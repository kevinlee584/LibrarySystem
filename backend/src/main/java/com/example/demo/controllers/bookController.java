package com.example.demo.controllers;

import com.example.demo.common.Message;
import com.example.demo.common.MessageStatus;
import com.example.demo.common.Pair;
import com.example.demo.models.Book;
import com.example.demo.models.Inventory;
import com.example.demo.models.Record;
import com.example.demo.services.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/book")
public class bookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestParam(value="ISBN") String isbn,
                                     @RequestParam(value="name") String name,
                                     @RequestParam(value="author") String author,
                                     @RequestParam(value="introduction") String introduction,
                                     @RequestParam(value="status") String status) {
        bookService.addBook(isbn, name, author, introduction, status);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeBook(Integer inventoryId) {
        bookService.removeBook(inventoryId);
        return ResponseEntity.ok("");
    }

    @PutMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestParam(value="inventoryId") Integer inventoryId,
                                        HttpServletRequest request) {
        Message log = bookService.borrowBook(inventoryId, (String)request.getAttribute("PhoneNumber"));

        return switch (log.getStatus()){
            case success, fail -> ResponseEntity.ok(log.getMessage());
            default -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(log.getMessage());
        };
    }

    @PutMapping("/return")
    public ResponseEntity<?> returnBook(@RequestParam(value="inventoryId") Integer inventoryId,
                                        HttpServletRequest request) {
        Message log = bookService.returnBook(inventoryId, (String)request.getAttribute("PhoneNumber"));

        return switch (log.getStatus()){
            case success, fail -> ResponseEntity.ok(log.getMessage());
            default -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(log.getMessage());
        };
    }

    @GetMapping("/all")
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/inv/{isbn}")
    public List<Record> showInventoryByISBN(@PathVariable(value="isbn") String isbn){
        return bookService.getInventory(isbn);
    }

    @GetMapping("/{isbn}")
    public Book showBookByISBN(@PathVariable(value="isbn") String isbn){
        return bookService.getBook(isbn);
    }

    @GetMapping("/show/record")
    public ResponseEntity<List<Record>> showBorrowedBooksByPhoneNumber(HttpServletRequest request){
        Pair<Message, List<Record>> pair = bookService.getRecords((String)request.getAttribute("PhoneNumber"));
        return switch (pair.getLeft().getStatus()){
            case success, fail -> ResponseEntity.ok(pair.getRight());
            default -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pair.getRight());
        };
    }
}
