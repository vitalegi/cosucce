package it.vitalegi.cosucce.resource;

import io.swagger.v3.oas.annotations.Operation;
import it.vitalegi.cosucce.user.dto.OtpResponse;
import it.vitalegi.cosucce.user.dto.User;
import it.vitalegi.cosucce.user.dto.UserOtp;
import it.vitalegi.cosucce.user.service.UserService;
import it.vitalegi.metrics.Performance;
import it.vitalegi.metrics.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Performance(Type.ENDPOINT)
public class UserResource {

    @Autowired
    UserService userService;

    @Operation(summary = "Create new OTP")
    @PutMapping(path = "/otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserOtp addOtp() {
        return userService.addOtp();
    }

    @Operation(summary = "Deletes connection to Telegram")
    @DeleteMapping(path = "/telegram", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public void deleteTelegramConnection() {
        User user = userService.getCurrentUser();
        userService.updateTelegramUserId(user.getId(), null);
    }

    @Operation(summary = "Retrieve user data")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser() {
        userService.importCurrentUser();
        return userService.getCurrentUser();
    }

    @Operation(summary = "Change username")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUsername(@RequestBody User user) {
        return userService.updateUsername(user.getUsername());
    }

    @Operation(summary = "Consumes an OTP")
    @PostMapping(path = "/otp/{otp}", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public OtpResponse useOtp(@PathVariable("otp") String otp) {
        User user = userService.getCurrentUser();
        return userService.useOtp(user.getId(), otp);
    }
}
