package it.vitalegi.cosucce.iam.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.constants.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {App.class})
@Slf4j
@ActiveProfiles("test")
public class UserServiceTests {

    @Autowired
    UserService userService;

    @Nested
    class Build {
        @Test
        void given_newUser_then_userIsCreated() {
            var extId = UUID.randomUUID();
            var user = userService.build("issuer", extId.toString());
            assertNotNull(user);
            assertNotNull(user.getId());
            assertNotEquals(user.getId(), extId);
            assertEquals(UserStatus.ACTIVE, user.getStatus());
        }

        @Test
        void given_userWithDifferentIssuer_then_userIsCreated() {
            var extId = UUID.randomUUID();
            var user1 = userService.build("issuer1", extId.toString());
            var user2 = userService.build("issuer2", extId.toString());
            assertNotEquals(user2.getId(), user1.getId());
        }

        @Test
        void given_userWithDifferentIds_then_userIsCreated() {
            var extId1 = UUID.randomUUID();
            var extId2 = UUID.randomUUID();
            var user1 = userService.build("issuer", extId1.toString());
            var user2 = userService.build("issuer", extId2.toString());
            assertNotEquals(user2.getId(), user1.getId());
        }

        @Test
        void given_existingUser_then_existingUserIsReturned() {
            var extId = UUID.randomUUID();
            var user1 = userService.build("issuer", extId.toString());
            var user2 = userService.build("issuer", extId.toString());
            assertEquals(user2.getId(), user1.getId());
        }
    }
}
