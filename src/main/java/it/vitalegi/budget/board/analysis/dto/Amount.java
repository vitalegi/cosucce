package it.vitalegi.budget.board.analysis.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Amount {

    BigDecimal actual;
    BigDecimal expected;

    public synchronized void addActual(BigDecimal value) {
        actual = actual.add(value);
    }
}
