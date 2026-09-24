package it.vitalegi.cosucce.board.dto.analysis;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MonthlyUserAnalysis {
    @NotNull
    int year;
    @NotNull
    int month;
    @NotNull
    List<UserAmount> users;
}
