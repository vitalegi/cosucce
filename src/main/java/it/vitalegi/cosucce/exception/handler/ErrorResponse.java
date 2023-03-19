package it.vitalegi.cosucce.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    String error;
    String message;
}