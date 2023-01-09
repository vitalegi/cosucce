package it.vitalegi.budget.it;


import com.fasterxml.jackson.core.type.TypeReference;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.user.dto.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class BoardIT {

    final String USER1 = "user1";
    final String USER2 = "user2";
    final String USER3 = "user3";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    MockAuth mockAuth;

    @Autowired
    CallService cs;

    @Test
    void test_addBoard_shouldCreateBoard() throws Exception {
        RequestPostProcessor auth = mockAuth.user(USER1);
        User user = accessOk(auth, USER1);
        addBoardOk(auth, "board1", user.getId());
    }

    @Test
    void test_getBoard_owned_shouldReturnBoard() throws Exception {
        RequestPostProcessor auth = mockAuth.user(USER1);
        User user = accessOk(auth, USER1);
        Board board1 = addBoardOk(auth, "board1", user.getId());
        Board board = getBoardOk(auth, board1.getId().toString());
        validateBoard("board1", user.getId(), board1.getId(), board);
    }


    @Test
    void test_getBoard_member_shouldReturnBoard() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        addBoardUserOk(auth1, board1.getId(), user2.getId(), BoardUserRole.MEMBER);
        Board board = getBoardOk(auth2, board1.getId().toString());
        validateBoard("board1", user1.getId(), board1.getId(), board);
    }

    @Test
    void test_getBoard_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        getBoard(auth2, board1.getId().toString()).andExpect(error403());
    }


    @Test
    void test_getBoards_shouldReturnBoards() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board1", user1.getId());
        log.info("user1 is owner of board1");

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        Board board2 = addBoardOk(auth2, "board2", user2.getId());
        addBoardUserOk(auth2, board2.getId(), user1.getId(), BoardUserRole.MEMBER);
        log.info("user1 is member of board2");

        RequestPostProcessor auth3 = mockAuth.user(USER3);
        User user3 = accessOk(auth3, USER3);
        Board board3 = addBoardOk(auth3, "board3", user3.getId());
        log.info("user1 is not in board3");

        List<Board> boards = cs.jsonPayloadList(getBoards(auth1).andExpect(ok()), new TypeReference<List<Board>>() {
        });
        assertEquals(2, boards.size());
        Board out1 = boards.stream().filter(b -> b.getId().equals(board1.getId())).findFirst().orElseThrow(() -> new NoSuchElementException("Missing board1"));
        Board out2 = boards.stream().filter(b -> b.getId().equals(board2.getId())).findFirst().orElseThrow(() -> new NoSuchElementException("Missing board2"));
        validateBoard(board1.getName(), board1.getOwnerId(), board1.getId(), out1);
        validateBoard(board2.getName(), board2.getOwnerId(), board2.getId(), out2);
    }


    @Test
    void test_addBoardEntry_owner_shouldCreateEntry() throws Exception {
        RequestPostProcessor auth = mockAuth.user(USER1);
        User user = accessOk(auth, USER1);
        Board board = addBoardOk(auth, "board1", user.getId());
        BoardEntry entry = addBoardEntryOk(auth, board.getId(), user.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description");
        validateBoardEntry(board.getId(), user.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description", entry);
    }

    @Test
    void test_addBoardEntry_member_shouldCreateEntry() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board1.getId(), user2.getId(), BoardUserRole.MEMBER);

        BoardEntry entry = addBoardEntryOk(auth2, board1.getId(), user2.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description");
        validateBoardEntry(board1.getId(), user2.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description", entry);
    }

    @Test
    void test_addBoardEntry_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        addBoardEntry(auth2, board1.getId(), user2.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description") //
                .andExpect(error403());
    }


    @Test
    void test_addBoardUser_owner_shouldAssign() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        BoardUser userRole = addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);
        validateBoardUser(user2.getId(), BoardUserRole.MEMBER, userRole);

        userRole = addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.OWNER);
        validateBoardUser(user2.getId(), BoardUserRole.OWNER, userRole);
    }

    @Test
    void test_addBoardUser_member_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);

        RequestPostProcessor auth3 = mockAuth.user(USER3);
        User user3 = accessOk(auth3, USER3);
        addBoardUser(auth2, board.getId(), user3.getId(), BoardUserRole.MEMBER).andExpect(error403());
    }

    @Test
    void test_addBoardUser_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUser(auth2, board.getId(), user2.getId(), BoardUserRole.MEMBER).andExpect(error403());
    }


    User accessOk(RequestPostProcessor user, String uid) throws Exception {
        User out = cs.jsonPayload(access(user).andExpect(ok()), User.class);
        validateUser(null, uid, out);
        return out;
    }

    ResultActions access(RequestPostProcessor user) throws Exception {
        return mockMvc.perform(get("/user").with(user));
    }

    Board addBoardOk(RequestPostProcessor auth, String boardName, Long ownerId) throws Exception {
        Board board = cs.jsonPayload(addBoard(auth, boardName).andExpect(ok()), Board.class);
        validateBoard(boardName, ownerId, null, board);
        return board;
    }

    ResultActions addBoard(RequestPostProcessor user, String name) throws Exception {
        Board request = new Board();
        request.setName(name);

        return mockMvc.perform(put("/board") //
                .with(csrf()) //
                .with(user) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    void validateBoard(String name, Long ownerId, UUID boardId, Board actual) {
        assertNotNull(actual.getId());
        if (boardId != null) {
            assertEquals(boardId, actual.getId());
        }
        if (name != null) {
            assertEquals(name, actual.getName());
        }
        if (ownerId != null) {
            assertEquals(ownerId.longValue(), actual.getOwnerId());
        }
        assertNotNull(actual.getLastUpdate());
        assertNotNull(actual.getCreationDate());
    }

    Board getBoardOk(RequestPostProcessor user, String boardId) throws Exception {
        return cs.jsonPayload(getBoard(user, boardId).andExpect(ok()), Board.class);
    }

    ResultActions getBoard(RequestPostProcessor user, String boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId).with(user));
    }

    ResultActions getBoards(RequestPostProcessor user) throws Exception {
        return mockMvc.perform(get("/board").with(user));
    }

    void validateUser(Long id, String uid, User actual) {
        assertEquals(uid, actual.getUid());
        if (id != null) {
            assertEquals(id, actual.getId());
        }
        String username = mockAuth.username(uid);
        assertEquals(username, actual.getUsername());
    }


    BoardEntry addBoardEntryOk(RequestPostProcessor auth, UUID boardId, Long ownerId, LocalDate date, BigDecimal amount, String category, String description) throws Exception {
        return cs.jsonPayload(addBoardEntry(auth, boardId, ownerId, date, amount, category, description).andExpect(ok()), BoardEntry.class);
    }

    ResultActions addBoardEntry(RequestPostProcessor user, UUID boardId, Long ownerId, LocalDate date, BigDecimal amount, String category, String description) throws Exception {
        BoardEntry request = new BoardEntry();
        request.setBoardId(boardId);
        request.setOwnerId(ownerId);
        request.setAmount(amount);
        request.setDescription(description);
        request.setCategory(category);
        request.setDate(date);

        return mockMvc.perform(put("/board/" + boardId + "/entry") //
                .with(csrf()) //
                .with(user) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    BoardUser addBoardUserOk(RequestPostProcessor auth, UUID boardId, Long userId, BoardUserRole role) throws Exception {
        return cs.jsonPayload(addBoardUser(auth, boardId, userId, role).andExpect(ok()), BoardUser.class);
    }

    ResultActions addBoardUser(RequestPostProcessor user, UUID boardId, Long userId, BoardUserRole role) throws Exception {
        BoardUser request = new BoardUser();
        request.setRole(role);
        return mockMvc.perform(put("/board/" + boardId + "/user/" + userId) //
                .with(csrf()) //
                .with(user) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    ResultMatcher ok() {
        return status().isOk();
    }

    ResultMatcher error500() {
        return status().isInternalServerError();
    }

    ResultMatcher error403() {
        return status().isForbidden();
    }

    void validateBoardEntry(UUID boardId, Long ownerId, LocalDate date, BigDecimal amount, String category, String description, BoardEntry actual) {
        assertEquals(boardId, actual.getBoardId());
        assertEquals(ownerId, actual.getOwnerId());
        assertEquals(date, actual.getDate());
        assertEquals(amount, actual.getAmount());
        assertEquals(category, actual.getCategory());
        assertEquals(description, actual.getDescription());
    }

    void validateBoardUser(Long userId, BoardUserRole role, BoardUser actual) {
        assertEquals(userId, actual.getUser().getId());
        assertEquals(role, actual.getRole());
    }
}