package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Board {
    private UUID boardId;
    private String name;
    private Instant creationDate;
    private Instant lastUpdate;
}
