package it.vitalegi.cosucce.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.vitalegi.cosucce.exception.PermissionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log4j2
@Component
@ControllerAdvice
public class ResponseExceptionHandler {
    @Autowired
    ObjectMapper mapper;

    @Value("${log.skip-classes}")
    List<String> skipClasses;

    @Value("${log.single-line}")
    boolean singleLine;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable e) {
        log(e);
        return new ResponseEntity<>(new ErrorResponse(e.getClass().getName()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ErrorResponse> handle(PermissionException e) {
        log(e);
        return new ResponseEntity<>(new ErrorResponse(e.getClass().getName()), HttpStatus.FORBIDDEN);
    }

    protected void log(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        String lineSeparator = ">>";
        if (!singleLine) {
            lineSeparator = "\n";
        }

        String error = Stream.of(sw.toString().split("\n"))
                             .filter(s -> !skipClasses.stream().anyMatch(skip -> s.contains(skip)))
                             .collect(Collectors.joining(lineSeparator));
        log.error(error);
    }
}