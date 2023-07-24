package it.vitalegi.cosucce.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.vitalegi.cosucce.board.constant.BoardUserRole;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class AddBoardUser {
    @NotNull
    @Schema(description = "Role to be assigned")
    BoardUserRole role;
}
