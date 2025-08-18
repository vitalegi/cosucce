package it.vitalegi.cosucce.budget.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddBoardDto {

    UUID boardId;
    String name;
    String etag;
}
