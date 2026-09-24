package it.vitalegi.cosucce.board.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BoardSplit {

    @NotNull
    UUID id;

    @NotNull
    long userId;
    @NotNull
    UUID boardId;
    Integer fromYear;
    @Min(1)
    @Max(12)
    Integer fromMonth;
    Integer toYear;
    @Min(1)
    @Max(12)
    Integer toMonth;
    @Min(0)
    @Max(1)
    BigDecimal value1;
}
