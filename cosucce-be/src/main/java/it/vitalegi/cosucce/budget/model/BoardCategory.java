package it.vitalegi.cosucce.budget.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BoardCategory {
    private UUID categoryId;
    private UUID boardId;
    private String label;
    private String icon;
    private boolean enabled;
    private Instant creationDate;
    private Instant lastUpdate;
}
