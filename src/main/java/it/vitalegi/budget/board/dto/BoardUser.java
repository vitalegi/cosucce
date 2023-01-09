package it.vitalegi.budget.board.dto;

import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.user.dto.User;
import lombok.Data;

@Data
public class BoardUser {
    User user;
    BoardUserRole role;
}
