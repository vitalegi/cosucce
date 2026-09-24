package it.vitalegi.cosucce.board.dto.analysis;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAmount extends Amount {
    @NotNull
    long userId;

}
