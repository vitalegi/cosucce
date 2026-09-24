package it.vitalegi.cosucce.board.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BoardEntry {
    @NotNull
    UUID id;
    @NotNull
    UUID boardId;
    @NotNull
    LocalDate date;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
    long ownerId;
    String category;
    String description;
    BigDecimal amount;
}
