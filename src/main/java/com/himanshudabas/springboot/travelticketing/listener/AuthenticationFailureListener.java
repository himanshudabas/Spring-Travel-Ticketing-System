package com.himanshudabas.springboot.travelticketing.listener;

import com.himanshudabas.springboot.travelticketing.service.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {
    private final LoginAttemptService loginAttemptService;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        LOGGER.info("[onAuthenticationFailure]");
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) principal;
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
