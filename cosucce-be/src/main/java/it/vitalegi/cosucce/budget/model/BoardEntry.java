package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class BoardEntry {
    private UUID entryId;
    private UUID boardId;
    private int version;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private UUID lastUpdatedBy;
    private Instant creationDate;
    private Instant lastUpdate;
}
