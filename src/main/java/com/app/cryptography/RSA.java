package com.app.cryptography;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

public abstract class RSA {
    // Генерация пары ключей
    public static KeyPair getKeys() {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        return kp;
    }

    // шифрование
    public static String encoding(String data, PublicKey publicKey) {
        String encodedData = "";
        if (data.length() > 117) {
            int size = data.length() / 117;
            if (data.length() % 117 != 0) {
                size++;
            }
            int i;
            for (i = 0; i < size - 1; i++) {
                String dataPart = data.substring(i * 117, i * 117 + 117);
                encodedData += encoding(dataPart, publicKey);
            }
            String dataPart = data.substring(i * 117);
            encodedData += encoding(dataPart, publicKey);
        }
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encodedBytes = c.doFinal(data.getBytes());
            encodedData = Base64.getEncoder().encodeToString(encodedBytes);
        } catch (Exception e) {
        }
        return encodedData;
    }

    // дешифрование
    public static String decoding(String data, PrivateKey privateKey) {
        String decodedData = "";
        if (data.length() > 172) {
            int size = data.length() / 172;
            for (int i = 0; i < size; i++) {
                String dataPart = data.substring(i * 172, i * 172 + 172);
                decodedData += decoding(dataPart, privateKey);
            }
        }
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = c.doFinal(Base64.getDecoder().decode(data));
            decodedData = new String(decodedBytes);
        } catch (Exception e) {
        }
        return decodedData;
    }
}
