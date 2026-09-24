package it.vitalegi.cosucce.board.dto;

import it.vitalegi.cosucce.board.constant.BoardUserRole;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class AddBoardUser {
    @NotNull
    BoardUserRole role;
}
