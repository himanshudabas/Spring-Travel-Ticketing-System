package com.himanshudabas.springboot.travelticketing.enumeration;

import com.himanshudabas.springboot.travelticketing.constant.Authority;

public enum Role {
    ROLE_USER(Authority.USER_AUTHORITIES),
    ROLE_ADMIN(Authority.ADMIN_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
