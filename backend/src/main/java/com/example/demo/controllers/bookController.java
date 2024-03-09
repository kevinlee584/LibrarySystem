package com.example.demo.controllers;

import com.example.demo.models.Book;
import com.example.demo.models.Inventory;
import com.example.demo.models.Record;
import com.example.demo.services.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
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
        if (bookService.borrowBook(inventoryId, (String)request.getAttribute("PhoneNumber")))
            return ResponseEntity.ok("success");
        else
            return ResponseEntity.ok("fail");
    }

    @PutMapping("/return")
    public ResponseEntity<?> returnBook(@RequestParam(value="inventoryId") Integer inventoryId,
                                        HttpServletRequest request) {
        bookService.returnBook(inventoryId, (String)request.getAttribute("PhoneNumber"));
        return ResponseEntity.ok("");
    }

    @GetMapping("/all")
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/{isbn}")
    public List<Record> showInventoryByISBN(@PathVariable(value="isbn") String isbn){
        System.out.print(isbn);
        return bookService.getInventory(isbn);
    }

    @GetMapping("/show/record")
    public List<Record> showBorrowedBooksByPhoneNumber(HttpServletRequest request){
        return bookService.getRecords((String)request.getAttribute("PhoneNumber"));
    }
}
