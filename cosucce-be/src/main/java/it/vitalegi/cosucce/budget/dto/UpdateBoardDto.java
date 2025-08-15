package it.vitalegi.cosucce.budget.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBoardDto {

    String name;
    int version;
}
