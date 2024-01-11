package com.example.gateway.filter;


public class UrlFilter {

    private static final String nointerceterurl = "/api/user/login,/api/user/register";

    public static boolean hasAutorize(String uri) {
        String[] split = nointerceterurl.split(",");
        for (String s : split) {
            if (s.equals(uri)) {
                return true;
            }
        }
        return false;
    }
}
