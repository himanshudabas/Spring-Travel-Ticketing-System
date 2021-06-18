package com.himanshudabas.springboot.travelticketing.exception.domain;

public class UnauthorizedTicketAccessException extends Exception {
    public UnauthorizedTicketAccessException(String msg) {
        super(msg);
    }
}
