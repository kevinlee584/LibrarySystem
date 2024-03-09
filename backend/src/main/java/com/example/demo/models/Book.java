package com.example.demo.models;

public class Book {
    private String ISBN;
    private String name;
    private String author;
    private String introduction;

    public Book(String ISBN, String name, String author, String introduction) {
        this.ISBN = ISBN;
        this.name = name;
        this.author = author;
        this.introduction = introduction;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN=" + ISBN +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}
