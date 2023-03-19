package it.vitalegi.cosucce.it;


import it.vitalegi.cosucce.board.repository.BoardRepository;
import it.vitalegi.cosucce.spando.dto.SpandoDays;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SpandoTests extends RestResources {

    final String USER1 = "user1";
    final String USER2 = "user2";
    final String USER3 = "user3";

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserOtpRepository userOtpRepository;
    @Autowired
    BoardRepository boardRepository;

    RequestPostProcessor auth1;
    User user1;
    RequestPostProcessor auth2;
    User user2;
    RequestPostProcessor auth3;
    User user3;

    @BeforeEach
    public void init() throws Exception {
        log.info("Initialize database, erase user data. Boards={}, Users={}", boardRepository.count(),
                userRepository.count());
        userOtpRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
        assertEquals(0, boardRepository.count());
        assertEquals(0, userRepository.count());

        auth1 = mockAuth.user(USER1);
        user1 = accessOk(auth1, USER1);

        auth2 = mockAuth.user(USER2);
        user2 = accessOk(auth2, USER2);

        auth3 = mockAuth.user(USER3);
        user3 = accessOk(auth3, USER3);
    }

    @DisplayName("GIVEN I am a user WHEN I update spando entry THEN entry is updated")
    @Test
    public void test_addSpandoEntry_changeEntry_shouldCreate() throws Exception {
        addSpandoEntryOk(auth1, date("2023-01-01"));
        addSpandoEntryOk(auth1, date("2023-01-01"));
        List<SpandoDays> entries = getSpandosOk(auth1);
        assertEquals(0, entries.size());
    }

    @DisplayName("GIVEN I am a user WHEN I add new spando entry THEN entry is created")
    @Test
    public void test_addSpandoEntry_newEntry_shouldCreate() throws Exception {
        addSpandoEntryOk(auth1, date("2023-01-01"));
        addSpandoEntryOk(auth1, date("2023-01-02"));
        List<SpandoDays> entries = getSpandosOk(auth1);
        assertEquals(1, entries.size());
        assertEquals(date("2023-01-01"), entries.get(0).getFrom());
        assertEquals(date("2023-01-02"), entries.get(0).getTo());
    }

    @DisplayName("GIVEN I am a user WHEN I delete spando entry THEN entry is deleted")
    @Test
    public void test_deleteSpandoEntry_shouldDelete() throws Exception {
        addSpandoEntryOk(auth1, date("2023-01-01"));
        addSpandoEntryOk(auth1, date("2023-01-02"));
        deleteSpandoEntryOk(auth1, date("2023-01-01"));
        List<SpandoDays> entries = getSpandosOk(auth1);
        assertEquals(1, entries.size());
        assertEquals(date("2023-01-02"), entries.get(0).getFrom());
        assertEquals(date("2023-01-02"), entries.get(0).getTo());
    }

    protected LocalDate date(String date) {
        return LocalDate.parse(date);
    }

}