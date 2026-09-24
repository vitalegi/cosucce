package it.vitalegi.cosucce.board.dto.analysis;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MonthlyAnalysis {
    @NotNull
    int year;
    @NotNull
    int month;
    @NotNull
    BigDecimal amount;
}
