package it.vitalegi.budget.board.dto.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MonthlyAnalysis {
    @NotNull
    @Schema(description = "Year referred by this entry")
    int year;
    @NotNull
    @Schema(description = "Month referred by this entry")
    int month;
    @NotNull
    @Schema(description = "Amount spent")
    BigDecimal amount;
}
