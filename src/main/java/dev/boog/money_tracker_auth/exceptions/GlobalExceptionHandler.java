package dev.boog.money_tracker_auth.exceptions;

import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.exceptions.response.*;
import dev.boog.money_tracker_auth.utils.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import java.util.*;
import java.util.logging.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredToken(Exception ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(Constants.Exceptions.EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(Exception ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(Constants.Exceptions.INVALID_TOKEN, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(Exception ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(Constants.Exceptions.BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationFields>> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex
                        .getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(e -> new ValidationFields(e.getField(), e.getDefaultMessage()))
                        .toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(Exception ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(Constants.Exceptions.MISSING_BODY, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(EmailAlreadyUsedException ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }



}
