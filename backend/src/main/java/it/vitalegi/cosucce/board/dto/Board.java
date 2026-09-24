package it.vitalegi.cosucce.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Board {
    @NotNull
    UUID id;
    @NotNull
    String name;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
}
