package it.vitalegi.cosucce.iam.resource;

import it.vitalegi.cosucce.iam.model.UserIdentity;
import it.vitalegi.cosucce.iam.model.UserPermissions;
import it.vitalegi.cosucce.iam.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthenticationResource {

    AuthenticationService authenticationService;

    @GetMapping("/identity")
    public UserIdentity identity() {
        return authenticationService.identity();
    }

    @GetMapping("/permissions")
    public UserPermissions permissions() {
        return new UserPermissions(authenticationService.getPermissions().toList());
    }
}
