package it.vitalegi.budget.board.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BoardEntry {
    UUID id;
    LocalDate date;
    String ownerId;
    String category;
    String description;
    BigDecimal amount;
}
