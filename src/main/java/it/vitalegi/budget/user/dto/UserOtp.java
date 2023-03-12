package it.vitalegi.budget.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.vitalegi.budget.user.constant.OtpStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserOtp {
    @Schema(description = "OTP ID")
    long id;
    @Schema(description = "User's ID")
    long userId;
    @Schema(description = "Validity of this OTP")
    LocalDateTime validTo;
    @Schema(description = "OTP value")
    String otp;
    @Schema(description = "OTP status")
    OtpStatus status;
}
