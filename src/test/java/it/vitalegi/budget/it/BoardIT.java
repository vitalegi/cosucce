package it.vitalegi.budget.it;


import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.it.matcher.IsUUID;
import it.vitalegi.budget.user.dto.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        getBoard(auth, board1.getId().toString()) //
                .andExpect(ok()) //
                .andExpectAll(validBoard("board1", user.getId(), board1.getId()));
    }


    @Disabled("To be implemented once the joinBoard service is implemented")
    @Test
    void test_getBoard_member_shouldReturnBoard() throws Exception {
        // TODO
    }

    @Test
    void test_getBoard_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        getBoard(auth2, board1.getId().toString()) //
                .andExpect(error403());
    }


    @Test
    void test_getBoards_owned_shouldReturnBoard() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        Board board2 = addBoardOk(auth2, "board2", user2.getId());
        // TODO user1 joins board2

        RequestPostProcessor auth3 = mockAuth.user(USER3);
        User user3 = accessOk(auth3, USER3);
        Board board3 = addBoardOk(auth3, "board3", user3.getId());

        List<Board> boards = cs.jsonPayloadList(getBoards(auth1).andExpect(ok()), Board.class);
        assertEquals(2, boards.size());
        Board out1 = boards.stream().filter(b -> b.getId().equals(board1.getId())).findFirst().orElseThrow(() -> new NoSuchElementException("Missing board1"));
        Board out2 = boards.stream().filter(b -> b.getId().equals(board2.getId())).findFirst().orElseThrow(() -> new NoSuchElementException("Missing board2"));
        assertEquals(board1, out1);
        assertEquals(board2, out2);
    }

    User accessOk(RequestPostProcessor user, String uid) throws Exception {
        return cs.jsonPayload(access(user).andExpect(ok()).andExpectAll(validUser(null, uid)), User.class);
    }

    ResultActions access(RequestPostProcessor user) throws Exception {
        return mockMvc.perform(get("/user").with(user));
    }

    Board addBoardOk(RequestPostProcessor auth, String boardName, Long ownerId) throws Exception {
        return cs.jsonPayload(addBoard(auth, boardName) //
                .andExpect(ok()) //
                .andExpectAll(validBoard(boardName, ownerId, null)), Board.class);
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

    ResultMatcher[] validBoard(String name, Long ownerId, UUID boardId) {
        List<ResultMatcher> matchers = new ArrayList<>();
        matchers.add(jsonPath("$.id", new IsUUID()));
        if (boardId != null) {
            matchers.add(jsonPath("$.id", equalTo(boardId.toString())));
        }
        if (name != null) {
            matchers.add(jsonPath("$.name", equalTo(name)));
        }
        if (ownerId != null) {
            matchers.add(jsonPath("$.ownerId", equalTo(ownerId.intValue())));
        }
        matchers.add(jsonPath("$.creationDate", notNullValue()));
        matchers.add(jsonPath("$.lastUpdate", notNullValue()));
        return matchers.toArray(new ResultMatcher[matchers.size()]);
    }

    ResultActions getBoard(RequestPostProcessor user, String boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId).with(user));
    }

    ResultActions getBoards(RequestPostProcessor user) throws Exception {
        return mockMvc.perform(get("/board").with(user));
    }

    ResultMatcher[] validUser(Long id, String uid) {
        List<ResultMatcher> matchers = new ArrayList<>();
        matchers.add(jsonPath("$.uid", equalTo(uid)));
        matchers.add(jsonPath("$.id", notNullValue()));
        if (id != null) {
            matchers.add(jsonPath("$.id", equalTo(id.intValue())));
        }
        matchers.add(jsonPath("$.username", equalTo(mockAuth.username(uid))));
        return matchers.toArray(new ResultMatcher[matchers.size()]);
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
}