package it.vitalegi.cosucce.board.dto.analysis;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Amount {

    @NotNull
    BigDecimal actual;
    @NotNull
    BigDecimal expected;
    @NotNull
    BigDecimal cumulatedCredit;

    public synchronized void addActual(BigDecimal value) {
        actual = actual.add(value);
    }
}
