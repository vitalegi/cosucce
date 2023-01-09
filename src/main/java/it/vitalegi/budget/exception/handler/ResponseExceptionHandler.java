package it.vitalegi.budget.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.vitalegi.budget.exception.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Component
@ControllerAdvice
public class ResponseExceptionHandler {
    @Autowired
    ObjectMapper mapper;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable e) {
        return new ResponseEntity<>(new ErrorResponse(e.getClass().getName(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ErrorResponse> handle(PermissionException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getClass().getName(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

}