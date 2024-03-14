package com.example.demo.services;

import com.example.demo.Exception.JwtTokenIsExpired;
import com.example.demo.common.FormatChecker;
import com.example.demo.common.Message;
import com.example.demo.common.MessageStatus;
import com.example.demo.common.Pair;
import com.example.demo.models.Book;
import com.example.demo.models.Record;
import com.example.demo.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.common.FormatChecker.*;

import java.util.Collections;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    public void addBook(String isbn, String name, String author, String introduction, String status) {
        if(isbnChecker(isbn) &&
                bookStatusChecker(status) &&
                userNameChecker(name) &&
                userNameChecker(author) &&
                bookIntroductionChecker(introduction))
            bookRepository.addBook(new Book(isbn, name, author, introduction), status);
    }
    public void removeBook(Integer inventoryId) {
        if(inventoryIdChecker(inventoryId))
            bookRepository.removeBook(inventoryId);
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }
    public Message borrowBook(Integer inventoryId, String phoneNumber)  {
        if(inventoryIdChecker(inventoryId) && phoneNumberChecker(phoneNumber))
            return bookRepository.borrowBook(inventoryId, phoneNumber);
        return new Message(MessageStatus.fail, "資料格式錯誤");
    }
    public Message returnBook(Integer inventoryId, String phoneNumber){
        if(inventoryIdChecker(inventoryId) && phoneNumberChecker(phoneNumber))
            return bookRepository.returnBook(inventoryId, phoneNumber);
        return new Message(MessageStatus.fail, "資料格式錯誤");
    }
    public List<Record> getInventory(String isbn) {
        if(isbnChecker(isbn))
            return bookRepository.getInventory(isbn);
        return List.of();
    }

    public Book getBook(String isbn) {
        if(isbnChecker(isbn))
            return bookRepository.getBook(isbn);
        return null;
    }
    public Pair<Message, List<Record>> getRecords(String phoneNumber) {
        if(phoneNumberChecker(phoneNumber))
            return bookRepository.getRecords(phoneNumber);
        return new Pair<>(new Message(MessageStatus.fail, "fail"), List.of());
    }
}
