package com.example.demo.services;

import com.example.demo.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;

@Service
public class JwtService {

    private String key_location;

    private String keystore_password;

    private String keystore_alias;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public JwtService(@Value("${key.location}") String key_location,
                      @Value("${key.password}") String keystore_password,
                      @Value("${key.alias}") String keystore_alias) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.key_location = key_location;
        this.keystore_password = keystore_password;
        this.keystore_alias = keystore_alias;

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(key_location);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, keystore_password.toCharArray());
        privateKey = (PrivateKey) keyStore.getKey(keystore_alias, keystore_password.toCharArray());
        publicKey = keyStore.getCertificate(keystore_password).getPublicKey();

    }

    public String parseUserPhoneNumber(String token){
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
        Claims claims =parser.parseClaimsJws(token).getBody();
        return String.valueOf(claims.get("phoneNumber"));
    }

    public String generateToken(String phoneNumber) {
        return Jwts.builder().setClaims(Map.of("phoneNumber", phoneNumber))
                .signWith(SignatureAlgorithm.RS512, privateKey).compact();
    }
}
