package com.example.demo.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class JwtRepository {

    @Autowired
    private JdbcTemplate template;
    public boolean isJwtExpired(String phoneNumber){
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("is_jwt_expired");
        SqlParameterSource in = new MapSqlParameterSource() .addValue("PhoneNumber", phoneNumber);
        return (boolean)simpleJdbcCall.execute(in).get("is_expired");
    }

    public void removeJwt(String phoneNumber){
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("remove_jwt_token_by_phone");
        SqlParameterSource in = new MapSqlParameterSource() .addValue("PhoneNumber", phoneNumber);
        simpleJdbcCall.execute(in);
    }
}
