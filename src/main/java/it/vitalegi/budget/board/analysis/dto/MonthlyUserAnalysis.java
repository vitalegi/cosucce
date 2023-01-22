package it.vitalegi.budget.board.analysis.dto;

import lombok.Data;

import java.util.List;

@Data
public class MonthlyUserAnalysis {
    int year;
    int month;
    List<UserAmount> users;
}
