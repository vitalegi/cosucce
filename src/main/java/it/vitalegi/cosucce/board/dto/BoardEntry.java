package it.vitalegi.cosucce.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BoardEntry {
    @NotNull
    @Schema(description = "ID of the entry")
    UUID id;
    @NotNull
    @Schema(description = "ID of the board")
    UUID boardId;
    @NotNull
    @Schema(description = "Reference date")
    LocalDate date;
    @Schema(description = "Creation date")
    LocalDateTime creationDate;
    @Schema(description = "Last update")
    LocalDateTime lastUpdate;
    @Schema(description = "User to whom the entry is assigned to")
    long ownerId;
    @Schema(description = "Category of the entry")
    String category;
    @Schema(description = "Free text to describe the content of the entry")
    String description;
    @Schema(description = "Amount")
    BigDecimal amount;
}
