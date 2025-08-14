package it.vitalegi.cosucce.budget.exception;

import java.util.UUID;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(UUID boardId, Exception e) {
        super("Board " + boardId + " not found", e);
    }
}
