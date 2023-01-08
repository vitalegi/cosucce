package it.vitalegi.budget.board.dto;

import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.entity.UserEntity;
import lombok.Data;

import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BoardUser {
    User user;
    List<String> roles;
}
