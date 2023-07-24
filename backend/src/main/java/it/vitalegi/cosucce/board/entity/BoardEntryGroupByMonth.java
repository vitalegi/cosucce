package it.vitalegi.cosucce.board.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BoardEntryGroupByMonth {

    int year;

    int month;
    BigDecimal amount;

    public BoardEntryGroupByMonth(int year, int month, BigDecimal amount) {
        this.year = year;
        this.month = month;
        this.amount = amount;
    }
}
