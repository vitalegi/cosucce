package it.vitalegi.budget.board.analysis.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Amount {

    BigDecimal actual;
    BigDecimal expected;
    BigDecimal cumulatedCredit;

    public synchronized void addActual(BigDecimal value) {
        actual = actual.add(value);
    }
}
