package it.vitalegi.cosucce.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.vitalegi.cosucce.board.constant.BoardUserRole;
import it.vitalegi.cosucce.user.dto.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoardUser {
    @NotNull
    @Schema(description = "User")
    User user;
    @NotNull
    @Schema(description = "User's role")
    BoardUserRole role;
}
