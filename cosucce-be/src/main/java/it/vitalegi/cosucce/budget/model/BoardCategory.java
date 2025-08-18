package it.vitalegi.cosucce.budget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategory {
    private UUID categoryId;
    private UUID boardId;
    private String etag;
    private String label;
    private String icon;
    private boolean enabled;
    private Instant creationDate;
    private Instant lastUpdate;
}
