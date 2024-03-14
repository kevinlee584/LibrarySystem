package com.example.demo.repositories;

import com.example.demo.Exception.JwtTokenIsExpired;
import com.example.demo.models.Book;
import com.example.demo.models.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
                    Timestamp borrowTime = rs.getTimestamp("BorrowTime");
                    Timestamp returnTime = rs.getTimestamp("ReturnTime");
                    return new Record(inventoryId, name, author, borrowTime, returnTime, status);
                });

        SqlParameterSource in = new MapSqlParameterSource().addValue("ISBN", isbn);
        Map<String, Object> out = simpleJdbcCall.execute(in);
        return (List<Record>)(out.get("mapRef"));
    }

    public Book getBook(String isbn) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("find_book_by_ISBN")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    String name = rs.getString("Name");
                    String author = rs.getString("Author");
                    String introduction = rs.getString("Introduction");
                    return new Book(isbn, name, author, introduction);
                });

        SqlParameterSource in = new MapSqlParameterSource().addValue("ISBN", isbn);
        Map<String, Object> out = simpleJdbcCall.execute(in);
        List<Book> books = (List<Book>)out.get("mapRef");
        return books.size() == 0 ? null : books.get(0);
    }

    @Transactional
    public String borrowBook(Integer inventoryId, String phoneNumber) throws JwtTokenIsExpired {

        boolean isExpired = jwtRepository.isJwtExpired(phoneNumber);

        if(isExpired)  throw new JwtTokenIsExpired( "expired|請從新登入");

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
            return (boolean) simpleJdbcCall.execute(in).get("is_success") ? "success|借書成功":"fail|借書失敗";
        }catch (Exception e) {
            return "fail|借書失敗";
        }

    }

    @Transactional
    public String returnBook(Integer inventoryId, String phoneNumber) throws JwtTokenIsExpired {
        boolean isExpired = jwtRepository.isJwtExpired(phoneNumber);

        if(isExpired)  throw new JwtTokenIsExpired( "expired|請從新登入");

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("return_book");
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("InventoryId", inventoryId)
                .addValue("PhoneNumber", phoneNumber);
        try {
            simpleJdbcCall.execute(in);
            return "success|還書成功";
        }catch (Exception e) {
            return "fail|還書失敗";
        }
    }

    @Transactional
    public List<Record> getRecords(String phoneNumber) throws JwtTokenIsExpired{
        boolean isExpired = jwtRepository.isJwtExpired(phoneNumber);

        if(isExpired)  throw new JwtTokenIsExpired( "expired|請從新登入");

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("get_record_by_phone")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    Integer inventoryId = rs.getInt("InventoryId");
                    String name = rs.getString("Name");
                    String author = rs.getString("Author");
                    String status = rs.getString("Status");
                    Timestamp borrowTime = rs.getTimestamp("BorrowTime");
                    Timestamp returnTime = rs.getTimestamp("ReturnTime");
                    return new Record(inventoryId, name, author, borrowTime, returnTime, status);
                });
        SqlParameterSource in = new MapSqlParameterSource().addValue("PhoneNumber", phoneNumber);
        Map<String, Object> out = simpleJdbcCall.execute(in);
        return (List<Record>)(out.get("mapRef"));
    }

}
