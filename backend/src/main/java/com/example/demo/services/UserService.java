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

    public boolean addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.signUp(user);
    }

    public String userSignIn(User user){
        String token = jwtService.generateToken(user);
        if (userRepository.signIn(user, token))
            return token;
        else
            return "";
    }

    public boolean userSignOut(User user){
        return userRepository.signOut(user);
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber);
    }
}
