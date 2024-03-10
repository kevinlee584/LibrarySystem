package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    public boolean addUser(String password,String username, String phoneNumber, String role) {
        if(!username.matches("^\\w{4,20}$") || !password.matches("^[\\da-zA-Z]{4,20}$") || !phoneNumber.matches("^\\d{10}$"))
            return false;

        return userRepository.signUp(passwordEncoder.encode(password), username, phoneNumber, role);
    }

    public String userSignIn(String password, String phoneNumber){
        if(!password.matches("^[\\da-zA-Z]{4,20}$") || !phoneNumber.matches("^\\d{10}$"))
            return "";

        String token = jwtService.generateToken(phoneNumber);
        return userRepository.signIn(password, phoneNumber, token) ? token : "";
    }

    public boolean userSignOut(User user){
        return userRepository.signOut(user);
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber);
    }
}
