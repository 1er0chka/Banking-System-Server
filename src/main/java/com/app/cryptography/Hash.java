package com.app.cryptography;

import com.app.model.User;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    // Хэширование строки
    public static String getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

    // Хэширование данных пользователя (пароля)
    public static User hashUser(User user) throws NoSuchAlgorithmException {
        user.setPassword(Hash.getSHA(user.getPassword()));
        return user;
    }
}
