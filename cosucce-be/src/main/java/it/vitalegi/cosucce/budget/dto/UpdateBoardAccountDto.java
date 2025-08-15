package it.vitalegi.cosucce.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoardAccountDto {
    String label;
    String icon;
    boolean enabled;
    int version;
}
