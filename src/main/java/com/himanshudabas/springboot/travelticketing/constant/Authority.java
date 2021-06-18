package com.himanshudabas.springboot.travelticketing.constant;

public class Authority {
    public static final String[] USER_AUTHORITIES = {"users:read"};
    public static final String[] ADMIN_AUTHORITIES = {"users:read", "users:create", "users:update"};
}
