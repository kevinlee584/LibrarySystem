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

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "Requestor-Type", exposedHeaders = "X-Get-Header")
@RestController
public class authController {

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestParam(value="phoneNumber") String phoneNumber,
                                         @RequestParam(value="password") String password) {
        String jwtToken = userService.userSignIn(password, phoneNumber);
        if (jwtToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("phoneNumber or password is incorrect");
        else {
            return ResponseEntity.ok().header("Authorization", jwtToken).body("");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestParam(value="username") String username,
                                    @RequestParam(value="password") String password,
                                    @RequestParam(value="phoneNumber") String phoneNumber) {

        if (userService.addUser(password, username, phoneNumber, "ROLE_USER")) {
            return ResponseEntity.ok().body("signup success");
        }else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This PhoneNumber has been used");

    }
}
