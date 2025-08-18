package it.vitalegi.cosucce.budget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardEntry {
    private UUID entryId;
    private UUID boardId;
    private String etag;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private UUID lastUpdatedBy;
    private Instant creationDate;
    private Instant lastUpdate;
}
