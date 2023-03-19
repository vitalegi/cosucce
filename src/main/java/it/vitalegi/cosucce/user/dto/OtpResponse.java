package it.vitalegi.cosucce.user.dto;

import lombok.Data;

@Data
public class OtpResponse {
    boolean status;

    UserOtp otp;

    public static OtpResponse ko() {
        OtpResponse response = new OtpResponse();
        response.setStatus(false);
        return response;
    }

    public static OtpResponse ok(UserOtp otp) {
        OtpResponse response = new OtpResponse();
        response.setStatus(true);
        response.setOtp(otp);
        return response;
    }
}
