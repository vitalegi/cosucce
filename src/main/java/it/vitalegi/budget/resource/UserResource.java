package it.vitalegi.budget.resource;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Performance(Type.ENDPOINT)
public class UserResource {

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping(path = "/name")
    public String getName() {
        return authenticationService.getName();
    }

    @GetMapping(path = "/id")
    public String getId() {
        return authenticationService.getId();
    }
}
