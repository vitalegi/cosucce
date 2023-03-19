package it.vitalegi.cosucce.it;


import it.vitalegi.cosucce.user.dto.User;
import it.vitalegi.cosucce.user.repository.UserOtpRepository;
import it.vitalegi.cosucce.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserTests extends RestResources {

    final String USER1 = "user1";
    final String USER2 = "user2";
    final String USER3 = "user3";

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserOtpRepository userOtpRepository;
    RequestPostProcessor auth1;
    User user1;
    RequestPostProcessor auth2;
    User user2;
    RequestPostProcessor auth3;
    User user3;

    @BeforeEach
    public void init() throws Exception {
        log.info("Initialize database, erase user data. Users={}", userRepository.count());
        userOtpRepository.deleteAll();
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());

        auth1 = mockAuth.user(USER1);
        user1 = accessOk(auth1, USER1);

        auth2 = mockAuth.user(USER2);
        user2 = accessOk(auth2, USER2);

        auth3 = mockAuth.user(USER3);
        user3 = accessOk(auth3, USER3);
    }

    @DisplayName("GIVEN I am a user WHEN I change my username THEN only my username should be changed")
    @Test
    public void test_updateUsername_shouldChangeName() throws Exception {
        String otherUsername = getUserOk(auth2).getUsername();

        User user = updateUsernameOk(auth1, "foo");
        assertEquals("foo", user.getUsername());

        User user1 = getUserOk(auth1);
        assertEquals("foo", user1.getUsername());

        User user2 = getUserOk(auth2);
        assertEquals(otherUsername, user2.getUsername());
    }

    @DisplayName("GIVEN I am a user with an expired OTP WHEN I use the OTP THEN the OTP cannot be used")
    @Test
    public void test_useOtp_expiredOtp_shouldReject() throws Exception {
        var otp1 = addUserOtpOk(auth1);
        Thread.sleep(7000);
        assertFalse(useOtpOk(auth1, otp1.getOtp()));
    }

    @DisplayName("GIVEN I am a user without a valid OTP WHEN I use the OTP THEN the OTP cannot be used")
    @Test
    public void test_useOtp_invalidOtp_shouldReject() throws Exception {
        var otp1 = addUserOtpOk(auth1);
        assertFalse(useOtpOk(auth2, otp1.getOtp()));
    }

    @DisplayName("GIVEN I am a user with a valid OTP WHEN I use the OTP THEN the OTP is consumed")
    @Test
    public void test_useOtp_validOtp_shouldConsumeIt() throws Exception {
        var otp1 = addUserOtpOk(auth1);
        assertTrue(useOtpOk(auth1, otp1.getOtp()));
        assertFalse(useOtpOk(auth1, otp1.getOtp()));
    }

    @DisplayName("GIVEN I am a user with a valid OTP WHEN I use the OTP THEN the OTP can be used")
    @Test
    public void test_useOtp_validOtp_shouldUseIt() throws Exception {
        var otp1 = addUserOtpOk(auth1);
        assertTrue(useOtpOk(auth1, otp1.getOtp()));
    }
}