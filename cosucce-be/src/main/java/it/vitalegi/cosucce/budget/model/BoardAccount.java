package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BoardAccount {
    private UUID accountId;
    private UUID boardId;
    private int version;
    private String label;
    private String icon;
    private boolean enabled;
    private Instant creationDate;
    private Instant lastUpdate;
}
