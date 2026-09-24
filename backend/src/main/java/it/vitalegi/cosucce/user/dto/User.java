package it.vitalegi.cosucce.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {
    @NotNull
    long id;
    @NotNull
    String uid;

    @NotBlank
    @NotNull
    String username;
    Long telegramUserId;
}
