package it.vitalegi.budget.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BoardEntry {
    UUID id;
    UUID boardId;
    LocalDate date;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
    String ownerId;
    String category;
    String description;
    BigDecimal amount;
}
