package it.vitalegi.cosucce.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoardEntryDto {
    LocalDate date;
    UUID accountId;
    UUID categoryId;
    String description;
    BigDecimal amount;
    String etag;
    String newETag;
}
