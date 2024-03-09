package com.example.demo;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.JwtService;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private UserService service;

	@Autowired
	private JwtService jwtService;
	@Test
	void contextLoads() {

	}

	@Test
	void contextLoads2() throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		String s = jwtService.generateToken(new User("", "", "0936990321", ""));
		System.out.println(s);
		System.out.println();
		System.out.println(jwtService.parseUserPhoneNumber(s));
	}
}
