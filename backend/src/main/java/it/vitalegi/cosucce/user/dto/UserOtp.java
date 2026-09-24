package it.vitalegi.cosucce.user.dto;

import it.vitalegi.cosucce.user.constant.OtpStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserOtp {
    long id;
    long userId;
    LocalDateTime validTo;
    String otp;
    OtpStatus status;
}
