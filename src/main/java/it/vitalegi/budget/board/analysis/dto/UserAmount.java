package it.vitalegi.budget.board.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserAmount extends Amount {
    @NotNull
    @Schema(description = "User ID")
    long userId;

}
