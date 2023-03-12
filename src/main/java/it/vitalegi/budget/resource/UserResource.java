package it.vitalegi.budget.resource;

import io.swagger.v3.oas.annotations.Operation;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.dto.OtpResponse;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.dto.UserOtp;
import it.vitalegi.budget.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
