package com.example.demo.repositories;

import com.example.demo.common.Message;
import com.example.demo.common.MessageStatus;
import com.example.demo.models.Book;
import com.example.demo.models.Record;
import com.example.demo.models.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepository {
    @Autowired
    private JdbcTemplate template;

    private static final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> {
        String name = "User" + rs.getInt("UserId");
        String comment = rs.getString("Comment");
        Integer rate = rs.getInt("Rate");
        return new Review(name, comment, rate);
    };
    public Message addReview(String isbn, String phoneNumber, String comment, Integer rate) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("add_review");

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PhoneNumber", phoneNumber)
                .addValue("ISBN", isbn)
                .addValue("Comment", comment)
                .addValue("Rate", rate);
        try {
            simpleJdbcCall.execute(in);
            return new Message(MessageStatus.success, MessageStatus.success.toString());
        }catch (Exception e) {
            return new Message(MessageStatus.repeated, MessageStatus.repeated.toString());
        }
    }

    public List<Review> getReviews(String isbn) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("get_reviews")
                .returningResultSet("mapRef", reviewRowMapper);

        SqlParameterSource in = new MapSqlParameterSource().addValue("ISBN", isbn);
        return (List<Review>)(simpleJdbcCall.execute(in).get("mapRef"));
    }
}
