package com.himanshudabas.springboot.travelticketing.exception.email;

public class SendEmailFailException extends Exception {
    public SendEmailFailException(String msg) {
        super(msg);
    }
}
