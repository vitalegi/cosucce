package it.vitalegi.cosucce.board.dto;

import it.vitalegi.cosucce.board.constant.BoardUserRole;
import it.vitalegi.cosucce.user.dto.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoardUser {
    @NotNull
    User user;
    @NotNull
    BoardUserRole role;
}
