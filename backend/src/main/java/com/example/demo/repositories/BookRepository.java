package com.example.demo.repositories;

import com.example.demo.Exception.JwtTokenIsExpired;
import com.example.demo.common.Message;
import com.example.demo.common.MessageStatus;
import com.example.demo.common.Pair;
import com.example.demo.models.Book;
import com.example.demo.models.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class BookRepository {
    @Autowired
    private JdbcTemplate template;

    @Autowired
    private JwtRepository jwtRepository;

    private static final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        String isbn = rs.getString("ISBN");
        String name = rs.getString("Name");
        String author = rs.getString("Author");
        String introduction = rs.getString("Introduction");
        return new Book(isbn, name, author, introduction);
    };

    private static final RowMapper<Record> recordRowMapper = (rs, rowNum) -> {
        Integer inventoryId = rs.getInt("InventoryId");
        String name = rs.getString("Name");
        String author = rs.getString("Author");
        String status = rs.getString("Status");
        Timestamp borrowTime = rs.getTimestamp("BorrowTime");
        Timestamp returnTime = rs.getTimestamp("ReturnTime");
        return new Record(inventoryId, name, author, borrowTime, returnTime, status);
    };

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
                .returningResultSet("mapRef", bookRowMapper);

        return (List<Book>)simpleJdbcCall.execute().get("mapRef");
    }
    public List<Record> getInventory(String isbn) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("find_inventory_by_ISBN")
                .returningResultSet("mapRef", recordRowMapper);
        SqlParameterSource in = new MapSqlParameterSource().addValue("ISBN", isbn);

        return (List<Record>)(simpleJdbcCall.execute(in).get("mapRef"));
    }

    public Book getBook(String isbn) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("find_book_by_ISBN")
                .returningResultSet("mapRef", bookRowMapper);

        SqlParameterSource in = new MapSqlParameterSource().addValue("ISBN", isbn);
        List<Book> books = (List<Book>)simpleJdbcCall.execute(in).get("mapRef");
        return books.isEmpty() ? null : books.get(0);
    }

    @Transactional
    public Message borrowBook(Integer inventoryId, String phoneNumber){

        boolean isExpired = jwtRepository.isJwtExpired(phoneNumber);

        if(isExpired)
            return new Message(MessageStatus.expired, "請從新登入");

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("borrow_book")
                .returningResultSet("mapRef", (rs, rowNum) -> rs.getString("Status"));
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PhoneNumber", phoneNumber)
                .addValue("InventoryId", inventoryId);
        try {
            if ((boolean) simpleJdbcCall.execute(in).get("is_success"))
                return new Message(MessageStatus.success, "借書成功");
            else
                return new Message(MessageStatus.fail, "借書失敗");
        }catch (Exception e) {
            return new Message(MessageStatus.expired, "借書失敗");
        }

    }

    @Transactional
    public Message returnBook(Integer inventoryId, String phoneNumber){

        boolean isExpired = jwtRepository.isJwtExpired(phoneNumber);

        if(isExpired)
            return new Message(MessageStatus.expired, "請從新登入");

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("return_book");
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("InventoryId", inventoryId)
                .addValue("PhoneNumber", phoneNumber);
        try {
            simpleJdbcCall.execute(in);
            return new Message(MessageStatus.success, "還書成功");
        }catch (Exception e) {
            return new Message(MessageStatus.fail, "還書失敗");
        }
    }

    @Transactional
    public Pair<Message, List<Record>> getRecords(String phoneNumber){
        boolean isExpired = jwtRepository.isJwtExpired(phoneNumber);

        if(isExpired)
            return new Pair<>(new Message(MessageStatus.expired, "請從新登入"), null);

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("get_record_by_phone")
                .returningResultSet("mapRef", recordRowMapper);

        SqlParameterSource in = new MapSqlParameterSource().addValue("PhoneNumber", phoneNumber);
        try {
            return new Pair<>(new Message(MessageStatus.success, "成功"),(List<Record>)(simpleJdbcCall.execute(in).get("mapRef")));
        }catch (Exception e) {
            return new Pair<>(new Message(MessageStatus.fail, "失敗"), null);
        }


    }

}
