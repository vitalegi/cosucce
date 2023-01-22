package it.vitalegi.budget.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Board {
    @NotNull
    @Schema(description = "ID of the board")
    UUID id;
    @NotNull
    @Schema(description = "Name of the board")
    String name;
    @Schema(description = "Creation date")
    LocalDateTime creationDate;
    @Schema(description = "Last update")
    LocalDateTime lastUpdate;
}
