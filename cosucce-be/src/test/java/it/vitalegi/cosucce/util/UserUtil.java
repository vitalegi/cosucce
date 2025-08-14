package it.vitalegi.cosucce.util;

import it.vitalegi.cosucce.iam.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@Service
@ActiveProfiles("test")
public class UserUtil {
    private static UserUtil _instance;
    @Autowired
    UserService userService;

    @PostConstruct
    public void init() {
        _instance = this;
    }

    public static UUID createUser() {
        return _instance.userService.build("issuer", UUID.randomUUID().toString()).getId();
    }
}
