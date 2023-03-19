package it.vitalegi.cosucce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    @NotNull
    @Schema(description = "User's ID")
    long id;
    @NotNull
    @Schema(description = "User's Firebase UID")
    String uid;

    @NotBlank
    @NotNull
    @Schema(description = "Username. Default value is the user's email.")
    String username;
    @Schema(description = "Telegram's userID, if available.")
    Long telegramUserId;
}
