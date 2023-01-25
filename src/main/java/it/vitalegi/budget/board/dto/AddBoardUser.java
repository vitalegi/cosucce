package it.vitalegi.budget.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.vitalegi.budget.board.constant.BoardUserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddBoardUser {
    @NotNull
    @Schema(description = "Role to be assigned")
    BoardUserRole role;
}
