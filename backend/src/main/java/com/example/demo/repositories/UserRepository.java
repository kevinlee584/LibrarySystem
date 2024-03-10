package com.example.demo.repositories;

import com.example.demo.models.User;
import com.zaxxer.hikari.util.SuspendResumeLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.*;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate template;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public boolean signUp(String password, String username, String phoneNumber, String role){

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("user_signup");

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PhoneNumber", phoneNumber)
                .addValue("Password", password)
                .addValue("UserName", username)
                .addValue("Role", role);
        try {
            simpleJdbcCall.execute(in);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    public boolean signIn(String rawPassword, String phoneNumber, String token) {

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("user_signin");
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PhoneNumber", phoneNumber)
                .addValue("Token", token);
        try {
            String storedPassword = (String)simpleJdbcCall.execute(in).get("pw");
            return passwordEncoder.matches(rawPassword, storedPassword);
        }catch (Exception e){
            return false;
        }
    }

    public boolean signOut(User user) {
        if(!String.valueOf(user.getPhoneNumber()).matches("^\\d{10}$"))
            return false;

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("user_signout");
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PhoneNumber", user.getPhoneNumber());
        try {
            simpleJdbcCall.execute(in);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template)
                .withProcedureName("get_user_by_phone_number")
                .returningResultSet("mapRef", (rs, rowNum) -> {
                    Integer UserId = rs.getInt("UserId");
                    String PhoneNumber = rs.getString("PhoneNumber");
                    String Password = rs.getString("Password");
                    String UserName = rs.getString("UserName");
                    Date RegistrationTime = rs.getDate("RegistrationTime");
                    Date LastLoginTime = rs.getDate("LastLoginTime");
                    String Role = rs.getString("Role");

                    User user = new User(Password, UserName, PhoneNumber, Role);
                    user.setId(UserId);
                    user.setLastLoginTime(LastLoginTime);
                    user.setRegistrationTime(RegistrationTime);
                    return user;
                });
        SqlParameterSource in = new MapSqlParameterSource().addValue("PhoneNumber", phoneNumber);
        try {
            Map<String, Object> out = simpleJdbcCall.execute(in);
            List<User> users = (List<User>)out.get("mapRef");
            return users.isEmpty() ? null: users.get(0);
        }catch (Exception e){
            return null;
        }
    }
}
