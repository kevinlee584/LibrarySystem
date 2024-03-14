package com.example.demo.services;

import com.example.demo.Exception.JwtTokenIsExpired;
import com.example.demo.common.Pair;
import com.example.demo.models.Book;
import com.example.demo.models.Record;
import com.example.demo.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BookService {
    private List<String> statuses = List.of("ALLOWED", "BORROWED", "BUSY", "LOST", "DAMAGED", "ABANDONED");
    @Autowired
    private BookRepository bookRepository;
    public void addBook(String isbn, String name, String author, String introduction, String status) {
        if(!isbn.matches("^\\d{3}-\\d-\\d{4}-\\d{4}-\\d$") || !isbn.matches("^\\d-\\d{4}-\\d{4}-\\d{1}$"))
            return;
        if (!statuses.contains(status))
            return;
        if(!name.matches("^\\w{0,50}$") || !author.matches("^\\w{0,50}$") || !introduction.matches("^\\w{0,1000}$"))
            return;
        bookRepository.addBook(new Book(isbn, name, author, introduction), status);
    }
    public void removeBook(Integer inventoryId) {
        if(!String.valueOf(inventoryId).matches("^\\d{0,50}$"))
            return;
        bookRepository.removeBook(inventoryId);
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }
    public String borrowBook(Integer inventoryId, String phoneNumber)  {
        if(!String.valueOf(inventoryId).matches("^\\d{0,50}$") || !String.valueOf(phoneNumber).matches("^\\d{10}$"))
            return "fail|資料格式錯誤";
        try {
            return bookRepository.borrowBook(inventoryId, phoneNumber);
        } catch (JwtTokenIsExpired e) {
            return e.getMessage();
        }
    }
    public String returnBook(Integer inventoryId, String phoneNumber){
        if(!String.valueOf(inventoryId).matches("^\\d{0,50}$"))
            return "fail|資料格式錯誤";
        try {
            return bookRepository.returnBook(inventoryId, phoneNumber);
        } catch (JwtTokenIsExpired e) {
            return e.getMessage();
        }
    }
    public List<Record> getInventory(String isbn) {
        if(!(isbn.matches("^\\d{3}-\\d-\\d{4}-\\d{4}-\\d$") || isbn.matches("^\\d-\\d{4}-\\d{4}-\\d{1}$")))
            return Collections.EMPTY_LIST;
        return bookRepository.getInventory(isbn);
    }

    public Book getBook(String isbn) {
        if(!(isbn.matches("^\\d{3}-\\d-\\d{4}-\\d{4}-\\d$") || isbn.matches("^\\d-\\d{4}-\\d{4}-\\d{1}$")))
            return null;
        return bookRepository.getBook(isbn);
    }
    public Pair<String, List<Record>> getRecords(String phoneNumber) {
        if(!phoneNumber.matches("^\\d{10}$"))
            return new Pair<>("fail|資料格式錯誤", Collections.EMPTY_LIST);

        try {
            return new Pair<>("success", bookRepository.getRecords(phoneNumber));
        } catch (JwtTokenIsExpired e) {
            return new Pair<>(e.getMessage(), Collections.EMPTY_LIST);
        }
    }
}
