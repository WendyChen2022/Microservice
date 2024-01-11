package com.example.common.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Assert;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;


public class JwtUtil {

    //2 hours
    public static final Long JWT_TTL = 7200000L;

    public static final String JWT_KEY = "demo-jwt";

    public static String getCurrentUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String currentUserId = getJwtInfo(auth);
        Assert.assertNotNull(currentUserId, "Login Check Failed");
        return currentUserId;
    }

    public static String getJwtInfo(String auth) {
        String currentUserId = "";
        try {
            Claims claims = JwtUtil.parseJWT(auth);
            String subject = claims.getSubject();
            HashMap<String, Object> userInfo = JSON.parseObject(subject, HashMap.class);
            currentUserId = String.valueOf(userInfo.get("id"));
            return currentUserId;
        } catch (Exception e) {
            throw new UnsupportedOperationException("JWT Check Failed");
        }
    }

    public static String createJWT(String id, String subject, Long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }

        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        SecretKey secretKey = generalKey();

        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setSubject(subject)
                .setIssuer("admin")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
        return builder.compact();
    }

    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(JwtUtil.JWT_KEY.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static void main(String[] args) {
        String jwt = JwtUtil.createJWT("d22fd66s2a6d2a6", "aaaaaa", null);
        System.out.println(jwt);
        try {
            Claims claims = JwtUtil.parseJWT(jwt);
            System.out.println(claims);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
