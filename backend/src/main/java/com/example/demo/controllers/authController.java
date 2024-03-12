package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.JwtService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class authController {

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestParam(value="phoneNumber") String phoneNumber,
                                         @RequestParam(value="password") String password) {
        String jwtToken = userService.userSignIn(password, phoneNumber);
        if (jwtToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail|電話或密碼錯誤");
        else {
            return ResponseEntity.ok().header("Authorization", jwtToken).body("");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestParam(value="username") String username,
                                    @RequestParam(value="password") String password,
                                    @RequestParam(value="phoneNumber") String phoneNumber) {

        int code = userService.addUser(password, username, phoneNumber, "ROLE_USER");

        if (code == 2) {
            return ResponseEntity.status(HttpStatus.CREATED).body("success");
        }else if (code == 1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail|電話已經被註冊");
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail|輸入資料格式錯誤");
    }
}
