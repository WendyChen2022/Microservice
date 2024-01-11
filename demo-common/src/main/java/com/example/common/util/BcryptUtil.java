package com.example.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
 

public class BcryptUtil {

    public static String doEncrypt(String password){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(password);
        return encodePassword;
    }

    public static boolean matchPassword(String password,String encodePassword){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean isTure = passwordEncoder.matches(password, encodePassword);
        return isTure;
    }
}