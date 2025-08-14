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
public class Board {
    private UUID boardId;
    private String name;
    private Instant creationDate;
    private Instant lastUpdate;
}
