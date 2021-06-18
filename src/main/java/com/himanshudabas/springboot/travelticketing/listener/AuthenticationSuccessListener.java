package com.himanshudabas.springboot.travelticketing.listener;

import com.himanshudabas.springboot.travelticketing.domain.UserPrincipal;
import com.himanshudabas.springboot.travelticketing.service.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        LOGGER.info("[onAuthenticationSuccess]");
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) principal;
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
