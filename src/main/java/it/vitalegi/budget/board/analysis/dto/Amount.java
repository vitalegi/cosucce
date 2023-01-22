package it.vitalegi.budget.board.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Amount {

    @NotNull
    @Schema(description = "Money spent by the user in the referred interval")
    BigDecimal actual;
    @NotNull
    @Schema(description = "Money that the user was expected to spend in the interval")
    BigDecimal expected;
    @NotNull
    @Schema(description = "Cumulated credit the user has so far. Value > 0 means that the user spent more than expected")
    BigDecimal cumulatedCredit;

    public synchronized void addActual(BigDecimal value) {
        actual = actual.add(value);
    }
}
