package it.vitalegi.cosucce.board.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BoardEntryGroupByMonthUserCategory {

    long userId;
    String category;

    int year;

    int month;
    BigDecimal amount;

    public BoardEntryGroupByMonthUserCategory(int year, int month, long userId, String category, BigDecimal amount) {
        this.year = year;
        this.month = month;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
    }
}
