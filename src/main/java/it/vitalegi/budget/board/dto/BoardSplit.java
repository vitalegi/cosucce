package it.vitalegi.budget.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BoardSplit {

    @NotNull
    @Schema(description = "ID of the split")
    UUID id;

    @NotNull
    @Schema(description = "User referred by this split")
    long userId;
    @NotNull
    @Schema(description = "Board ID")
    UUID boardId;
    @Schema(description = "Split is applied if the entry has date with year >= fromYear. The field is optional and, " +
            "if null, also fromMonth must be null.")
    Integer fromYear;
    @Schema(description = "Split is applied if the entry has date with month >= fromMonth. The field is optional and," +
            " if null, also fromYear must be null.")
    Integer fromMonth;
    @Schema(description = "Split is applied if the entry has date with year <= toYear. The field is optional and, if " +
            "null, also toMonth must be null.")
    Integer toYear;
    @Schema(description = "Split is applied if the entry has date with month <= toMonth. The field is optional and, " +
            "if null, also toMonth must be null.")
    Integer toMonth;
    @Min(0) @Max(1)
    @Schema(description = "Percentage to be applied for this split")
    BigDecimal value1;
}
