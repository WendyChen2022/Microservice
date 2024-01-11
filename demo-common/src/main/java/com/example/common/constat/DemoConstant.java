package com.example.common.constat;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemoConstant {

    public static String getJwtRedisKey(String userId) {
        return "iam:jwt:user:id:" + userId;
    }

    public static String getAuthRedisKey(String userId) {
        return "iam:" + userId;
    }
}
