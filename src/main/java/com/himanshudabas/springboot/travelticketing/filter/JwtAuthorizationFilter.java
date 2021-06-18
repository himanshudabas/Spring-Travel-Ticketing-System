package com.himanshudabas.springboot.travelticketing.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.himanshudabas.springboot.travelticketing.utility.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.himanshudabas.springboot.travelticketing.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.himanshudabas.springboot.travelticketing.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Component
@RestControllerAdvice
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider JWTTokenProvider;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public JwtAuthorizationFilter(JWTTokenProvider JWTTokenProvider) {
        this.JWTTokenProvider = JWTTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("[doFilterInternal]");
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username;
            try {
                username = JWTTokenProvider.getSubject(token);
            } catch (JWTDecodeException ignored) {
                filterChain.doFilter(request, response);
                return;
            }
            if (JWTTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = JWTTokenProvider.getAuthorities(token);
                Authentication authentication = JWTTokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

}
