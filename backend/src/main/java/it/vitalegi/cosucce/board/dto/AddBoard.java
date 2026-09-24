package it.vitalegi.cosucce.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBoard {

    @NotNull
    @NotBlank
    String name;
}
