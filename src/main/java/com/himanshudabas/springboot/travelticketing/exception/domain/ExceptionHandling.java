package com.himanshudabas.springboot.travelticketing.exception.domain;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.himanshudabas.springboot.travelticketing.domain.HttpResponse;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {

    public static final String ERROR_PATH = "/error";
    public static final String USERNAME_EMAIL_MISMATCH = "Username and email does not match";
    public static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    public static final String TICKET_IN_PROCESS_MESSAGE = "Ticket status is currently In Process. Cannot edit ticket details";
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed at this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / Password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NO_MAPPING = "There is no mapping for this url";
    private static final String TICKET_PERMISSION_ERROR = "You do not have access to this ticket";
    private static final String TICKET_NOT_FOUND_MESSAGE = "Ticket you are trying to access does not exist";
    private static final String TICKET_RESOLVE_INFO_MESSAGE = "Resolve info for the ticket is not Found";
    private static final String DOCUMENT_NOT_FOUND = "Document is not found";
    private static final String DOCUMENT_PERMISSION_ERROR = "You do not have permission to access this document";
    private static final String NO_CHANGE_RESOLVE_INFO = "There were no changes in your request";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(CONFLICT, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(NoChangeInResolveInfoException.class)
    public ResponseEntity<HttpResponse> noChangeInResolveInfoException() {
        return createHttpResponse(BAD_REQUEST, NO_CHANGE_RESOLVE_INFO);
    }

    @ExceptionHandler(UnauthorizedDocumentAccessException.class)
    public ResponseEntity<HttpResponse> unauthorizedDocumentAccessException() {
        return createHttpResponse(UNAUTHORIZED, DOCUMENT_PERMISSION_ERROR);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<HttpResponse> documentNotFoundException() {
        return createHttpResponse(NOT_FOUND, DOCUMENT_NOT_FOUND);
    }

    @ExceptionHandler(TicketResolveInfoNotFoundException.class)
    public ResponseEntity<HttpResponse> ticketResolveInfoNotFoundException() {
        return createHttpResponse(NOT_FOUND, TICKET_RESOLVE_INFO_MESSAGE);
    }

    @ExceptionHandler(TicketInProcessException.class)
    public ResponseEntity<HttpResponse> ticketInProcessException() {
        return createHttpResponse(BAD_REQUEST, TICKET_IN_PROCESS_MESSAGE);
    }

    @ExceptionHandler(UnauthorizedTicketAccessException.class)
    public ResponseEntity<HttpResponse> unauthorizedTicketAccessException() {
        return createHttpResponse(UNAUTHORIZED, TICKET_PERMISSION_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return createHttpResponse(BAD_REQUEST, Objects.requireNonNull(Objects.requireNonNull(exception.getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(UserDataException.class)
    public ResponseEntity<HttpResponse> userDataException(UserDataException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameEmailMismatchException.class)
    public ResponseEntity<HttpResponse> usernameEmailMismatchException() {
        return createHttpResponse(BAD_REQUEST, USERNAME_EMAIL_MISMATCH);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(BAD_REQUEST, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> usernameExistException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(EmployeeNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<HttpResponse> internalAuthenticationServiceException(InternalAuthenticationServiceException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getCause().getCause().getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(UNAUTHORIZED, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<HttpResponse> ticketNotFoundException() {
        return createHttpResponse(NOT_FOUND, TICKET_NOT_FOUND_MESSAGE);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<HttpResponse> jwtDecodeException() {
        return createHttpResponse(UNAUTHORIZED, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(SendEmailFailException.class)
    public ResponseEntity<HttpResponse> sendEmailFailException(SendEmailFailException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, NO_MAPPING);
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
