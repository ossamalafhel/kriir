package com.mobility.demo.exception;

import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;

@RestControllerAdvice
@Log
public class DisconnectExceptionHandler {

    @ExceptionHandler({ IOException.class })
    @ResponseStatus(HttpStatus.GONE)
    public void clientDisconnectException(IOException ex) {
        if(ex.getMessage().contains("Broken")){
            log.info("client disconnect");
        } else {
            log.severe(ex.getMessage());
        }
    }


}