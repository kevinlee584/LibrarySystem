package com.example.demo.repositories;

import com.example.demo.models.Book;
import com.example.demo.models.Inventory;
import com.example.demo.models.Record;
import com.example.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Repository
public class BookRepository {
    @Autowired
    private JdbcTemplate template;

    public void addBook(Book book, String status) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("add_book");
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("ISBN", book.getISBN())
                .addValue("Name", book.getName())
                .addValue("Author", book.getAuthor())
                .addValue("Introduction", book.getIntroduction())
                .addValue("Status", status);
        simpleJdbcCall.execute(in);
    }
    public void removeBook(Integer inventoryId) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("remove_book");
        SqlParameterSource in = new MapSqlParameterSource().addValue("InventoryId", inventoryId);
        simpleJdbcCall.execute(in);
    }

    public List<Book> getAllBooks() {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("get_all_books")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    String isbn = rs.getString("ISBN");
                    String name = rs.getString("Name");
                    String author = rs.getString("Author");
                    String introduction = rs.getString("Introduction");
                    return new Book(isbn, name, author, introduction);
                });
        Map<String, Object> out = simpleJdbcCall.execute();
        return (List<Book>)out.get("mapRef");
    }
    public List<Record> getInventory(String isbn) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("find_inventory_by_ISBN")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    Integer inventoryId = rs.getInt("InventoryId");
                    String name = rs.getString("Name");
                    String author = rs.getString("Author");
                    String status = rs.getString("Status");
                    Date borrowTime = rs.getDate("BorrowTime");
                    Date returnTime = rs.getDate("ReturnTime");
                    return new Record(inventoryId, name, author, borrowTime, returnTime, status);
                });

        SqlParameterSource in = new MapSqlParameterSource().addValue("ISBN", isbn);
        Map<String, Object> out = simpleJdbcCall.execute(in);
        return (List<Record>)(out.get("mapRef"));
    }

    public boolean borrowBook(Integer inventoryId, String phoneNumber) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("borrow_book")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    String status = rs.getString("Status");
                    return status;
                });

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PhoneNumber", phoneNumber)
                .addValue("InventoryId", inventoryId);
        try {
            List<String> out = (List<String>)simpleJdbcCall.execute(in).get("mapRef");
            if(out.size() == 1){
                return out.get(0).equals("ALLOWED");
            }
            return false;
        }catch (Exception e) {
            return false;
        }
    }

    public void returnBook(Integer inventoryId, String phoneNumber) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("return_book");
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("InventoryId", inventoryId)
                .addValue("PhoneNumber", phoneNumber);
        simpleJdbcCall.execute(in);
    }

    public List<Record> getRecords(String phoneNumber) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("get_record_by_phone")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    Integer inventoryId = rs.getInt("InventoryId");
                    String name = rs.getString("Name");
                    String author = rs.getString("Author");
                    String status = rs.getString("Status");
                    Date borrowTime = rs.getDate("BorrowTime");
                    Date returnTime = rs.getDate("ReturnTime");
                    return new Record(inventoryId, name, author, borrowTime, returnTime, status);
                });
        SqlParameterSource in = new MapSqlParameterSource().addValue("PhoneNumber", phoneNumber);
        Map<String, Object> out = simpleJdbcCall.execute(in);
        return (List<Record>)(out.get("mapRef"));
    }

}
