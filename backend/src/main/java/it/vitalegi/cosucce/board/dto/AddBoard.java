package it.vitalegi.cosucce.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AddBoard {

    @NotNull
    @NotBlank
    @Schema(description = "Name of the board")
    String name;
}
