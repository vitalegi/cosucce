package it.vitalegi.budget.board.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MonthlyUserAnalysis {
    @NotNull
    @Schema(description = "Year referred by this entry")
    int year;
    @NotNull
    @Schema(description = "Month referred by this entry")
    int month;
    @NotNull
    @Schema(description = "Users' analysis in this timeframe")
    List<UserAmount> users;
}
