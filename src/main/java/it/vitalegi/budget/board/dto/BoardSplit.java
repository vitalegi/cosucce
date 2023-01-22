package it.vitalegi.budget.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BoardSplit {
    UUID id;
    long userId;
    UUID boardId;
    Integer fromYear;
    Integer fromMonth;
    Integer toYear;
    Integer toMonth;
    BigDecimal value1;
}
