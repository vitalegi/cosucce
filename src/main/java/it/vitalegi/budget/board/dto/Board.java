package it.vitalegi.budget.board.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Board {
    UUID id;
    String name;
    String ownerId;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
}
