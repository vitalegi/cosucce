package it.vitalegi.budget.it;


import com.fasterxml.jackson.core.type.TypeReference;
import it.vitalegi.budget.board.analysis.dto.MonthlyUserAnalysis;
import it.vitalegi.budget.board.analysis.dto.UserAmount;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BoardTests {

    final String USER1 = "user1";
    final String USER2 = "user2";
    final String USER3 = "user3";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    MockAuth mockAuth;

    @Autowired
    CallService cs;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    @BeforeEach
    void init() {
        log.info("Initialize database, erase user data. Boards={}, Users={}", boardRepository.count(), userRepository.count());
        boardRepository.deleteAll();
        userRepository.deleteAll();
        assertEquals(0, boardRepository.count());
        assertEquals(0, userRepository.count());
    }

    @DisplayName("addBoard should create a new board")
    @Test
    void test_addBoard_shouldCreateBoard() throws Exception {
        RequestPostProcessor auth = mockAuth.user(USER1);
        User user = accessOk(auth, USER1);
        addBoardOk(auth, "board1", user.getId());
    }

    @DisplayName("getBoard, I'm unauthorized, should fail")
    @Test
    void test_getBoard_unknown_shouldReturnBoard() throws Exception {
        mockMvc.perform(get("/user")).andExpect(status().isUnauthorized());
    }

    @DisplayName("getBoard, I'm the owner, I should see the board")
    @Test
    void test_getBoard_owned_shouldReturnBoard() throws Exception {
        RequestPostProcessor auth = mockAuth.user(USER1);
        User user = accessOk(auth, USER1);
        Board board1 = addBoardOk(auth, "board1", user.getId());
        Board board = getBoardOk(auth, board1.getId().toString());
        validateBoard("board1", board1.getId(), board);
    }

    @DisplayName("getBoard, I'm a member, I should see the board")
    @Test
    void test_getBoard_member_shouldReturnBoard() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        addBoardUserOk(auth1, board1.getId(), user2.getId(), BoardUserRole.MEMBER);
        Board board = getBoardOk(auth2, board1.getId().toString());
        validateBoard("board1", board1.getId(), board);
    }

    @DisplayName("getBoard, I'm not part of the board, it should fail")
    @Test
    void test_getBoard_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board1 = addBoardOk(auth1, "board", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        getBoard(auth2, board1.getId().toString()).andExpect(error403());
    }

    @DisplayName("getBoards, multiple cases (owner, member, not a member)")
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
        validateBoard(board1.getName(), board1.getId(), out1);
        validateBoard(board2.getName(), board2.getId(), out2);
    }

    @DisplayName("addBoardEntry, I'm the owner, I should see the board")
    @Test
    void test_addBoardEntry_owner_shouldCreateEntry() throws Exception {
        RequestPostProcessor auth = mockAuth.user(USER1);
        User user = accessOk(auth, USER1);
        Board board = addBoardOk(auth, "board1", user.getId());
        BoardEntry entry = addBoardEntryOk(auth, board.getId(), user.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description");
        validateBoardEntry(board.getId(), user.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY", "description", entry);
    }

    @DisplayName("addBoardEntry, I'm a member, I should see the board")
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

    @DisplayName("addBoardEntry, I'm not a member, it should fail")
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


    @DisplayName("addBoardUser, I'm the owner, assignment should work")
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

    @DisplayName("addBoardUser, I'm a member, assignment should work")
    @Test
    void test_addBoardUser_member_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        BoardUser userRole = addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);
        validateBoardUser(user2.getId(), BoardUserRole.MEMBER, userRole);

        RequestPostProcessor auth3 = mockAuth.user(USER3);
        User user3 = accessOk(auth3, USER3);
        addBoardUser(auth2, board.getId(), user3.getId(), BoardUserRole.MEMBER).andExpect(error403());
    }

    @DisplayName("addBoardUser, I'm not a member, assignment should fail")
    @Test
    void test_addBoardUser_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUser(auth2, board.getId(), user2.getId(), BoardUserRole.MEMBER).andExpect(error403());
    }

    @DisplayName("getBoardUsers, I'm the owner, should work")
    @Test
    void test_getBoardUsers_owner_shouldWork() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        BoardUser userRole = addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);
        validateBoardUser(user2.getId(), BoardUserRole.MEMBER, userRole);

        List<BoardUser> users = getBoardUsersOk(auth1, board.getId());
        assertEquals(2, users.size());
        assertEquals(BoardUserRole.OWNER, users.stream().filter(u -> u.getUser().getId() == user1.getId()).findFirst().orElseThrow(() -> new NoSuchElementException("User1 not found")).getRole());
        assertEquals(BoardUserRole.MEMBER, users.stream().filter(u -> u.getUser().getId() == user2.getId()).findFirst().orElseThrow(() -> new NoSuchElementException("User2 not found")).getRole());
    }

    @DisplayName("getBoardUsers, I'm a member, should work")
    @Test
    void test_getBoardUsers_member_shouldWork() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);

        List<BoardUser> users = getBoardUsersOk(auth2, board.getId());
        assertEquals(2, users.size());
        assertEquals(BoardUserRole.OWNER, users.stream().filter(u -> u.getUser().getId() == user1.getId()).findFirst().orElseThrow(() -> new NoSuchElementException("User1 not found")).getRole());
        assertEquals(BoardUserRole.MEMBER, users.stream().filter(u -> u.getUser().getId() == user2.getId()).findFirst().orElseThrow(() -> new NoSuchElementException("User2 not found")).getRole());
    }

    @DisplayName("getBoardUsers, I'm not a member, should fail")
    @Test
    void test_getBoardUsers_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUser(auth2, board.getId(), user2.getId(), BoardUserRole.MEMBER).andExpect(error403());
    }

    @DisplayName("getBoardEntries, I'm the owner, should retrieve all entries")
    @Test
    void test_getBoardEntries_owner_shouldWork() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        BoardUser userRole = addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);
        validateBoardUser(user2.getId(), BoardUserRole.MEMBER, userRole);
        log.info("User2 is assigned to the board");

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 01, 05), new BigDecimal("123"), "CAT", null);
        addBoardEntry(auth2, board.getId(), user2.getId(), LocalDate.of(2022, 01, 10), new BigDecimal("456"), "CAT", null);
        List<BoardEntry> entries = getBoardEntriesOk(auth1, board.getId());
        assertEquals(2, entries.size());
    }

    @DisplayName("getBoardEntries, I'm a member, should retrieve all entries")
    @Test
    void test_getBoardEntries_member_shouldWork() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 01, 05), new BigDecimal("123"), "CAT", null);
        addBoardEntry(auth2, board.getId(), user2.getId(), LocalDate.of(2022, 01, 10), new BigDecimal("456"), "CAT", null);
        List<BoardEntry> entries = getBoardEntriesOk(auth2, board.getId());
        assertEquals(2, entries.size());
    }

    @DisplayName("getBoardEntries, I'm not a member, should fail")
    @Test
    void test_getBoardEntries_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUser(auth2, board.getId(), user2.getId(), BoardUserRole.MEMBER).andExpect(error403());

        RequestPostProcessor auth3 = mockAuth.user(USER3);
        User user3 = accessOk(auth3, USER3);

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 01, 05), new BigDecimal("123"), "CAT", null);
        addBoardEntry(auth2, board.getId(), user2.getId(), LocalDate.of(2022, 01, 10), new BigDecimal("456"), "CAT", null);
        getBoardEntries(auth3, board.getId()).andExpect(error403());
    }

    @DisplayName("getCategories, should retrieve all entries")
    @Test
    void test_getCategories_shouldWork() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 01, 05), new BigDecimal("123"), "CAT1", null);
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 01, 10), new BigDecimal("456"), "CAT2", null);
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 01, 10), new BigDecimal("456"), "CAT2", null);
        List<String> entries = getCategoriesOk(auth1, board.getId());
        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(c -> c.equals("CAT1")));
        assertTrue(entries.stream().anyMatch(c -> c.equals("CAT2")));
    }


    @DisplayName("addBoardSplit, I'm the owner, should create split")
    @Test
    void test_addBoardSplit_owner_shouldCreateSplit() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);

        BoardSplit s1 = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("1"));
        BoardSplit s2 = addBoardSplitOk(auth1, board.getId(), user1.getId(), 2022, 01, 2022, 02, new BigDecimal("0.5"));
        BoardSplit s3 = addBoardSplitOk(auth1, board.getId(), user2.getId(), 2022, 01, null, null, new BigDecimal("0.5"));
        BoardSplit s4 = addBoardSplitOk(auth1, board.getId(), user2.getId(), null, null, 2022, 02, new BigDecimal("0.5"));
        List<BoardSplit> splits = getBoardSplitsOk(auth1, board.getId());
        validateBoardSplits(Arrays.asList(s1, s2, s3, s4), splits);
    }

    @DisplayName("addBoardSplit, I'm a member, should fail")
    @Test
    void test_addBoardSplit_member_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);

        addBoardSplit(auth2, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("1")) //
                .andExpect(error403());
    }

    @DisplayName("addBoardSplit, I'm not a member, should fail")
    @Test
    void test_addBoardSplit_notMember_shouldFail() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);

        addBoardSplit(auth2, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("1")) //
                .andExpect(error403());
    }

    @DisplayName("getBoardAggregatedData, part of the board, should retrieve aggregated data")
    @Test
    void test_getBoardAggregatedData() throws Exception {
        RequestPostProcessor auth1 = mockAuth.user(USER1);
        User user1 = accessOk(auth1, USER1);
        Board board = addBoardOk(auth1, "board1", user1.getId());

        RequestPostProcessor auth2 = mockAuth.user(USER2);
        User user2 = accessOk(auth2, USER2);
        addBoardUserOk(auth1, board.getId(), user2.getId(), BoardUserRole.MEMBER);

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2023, 1, 05), new BigDecimal("1"), "CAT1", null);
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2023, 2, 05), new BigDecimal("1"), "CAT1", null);
        addBoardEntry(auth1, board.getId(), user2.getId(), LocalDate.of(2023, 2, 05), new BigDecimal("1"), "CAT1", null);

        addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("0.5"));
        addBoardSplitOk(auth1, board.getId(), user2.getId(), null, null, null, null, new BigDecimal("0.5"));

        List<MonthlyUserAnalysis> analysys = getBoardAnalysisMonthUserOk(auth1, board.getId());
        assertEquals(2, analysys.size());
        validateMonthlyUserAnalysis(2023, 01, Arrays.asList( //
                        userAmount(user1.getId(), "1", "0.5"), //
                        userAmount(user2.getId(), "0", "0.5")) //
                , analysys.get(0));
        validateMonthlyUserAnalysis(2023, 02, Arrays.asList( //
                        userAmount(user1.getId(), "1", "1"), //
                        userAmount(user2.getId(), "1", "1")) //
                , analysys.get(1));
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
        validateBoard(boardName, null, board);
        return board;
    }

    ResultActions addBoard(RequestPostProcessor user, String name) throws Exception {
        Board request = new Board();
        request.setName(name);

        return mockMvc.perform(post("/board") //
                .with(csrf()) //
                .with(user) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    void validateBoard(String name, UUID boardId, Board actual) {
        assertNotNull(actual.getId());
        if (boardId != null) {
            assertEquals(boardId, actual.getId());
        }
        if (name != null) {
            assertEquals(name, actual.getName());
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

        return mockMvc.perform(post("/board/" + boardId + "/entry") //
                .with(csrf()) //
                .with(user) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    List<BoardEntry> getBoardEntriesOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardEntries(auth, boardId).andExpect(ok()), new TypeReference<List<BoardEntry>>() {
        });
    }

    ResultActions getBoardEntries(RequestPostProcessor user, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/entries") //
                .with(user) //
        );
    }

    BoardUser addBoardUserOk(RequestPostProcessor auth, UUID boardId, Long userId, BoardUserRole role) throws Exception {
        return cs.jsonPayload(addBoardUser(auth, boardId, userId, role).andExpect(ok()), BoardUser.class);
    }

    ResultActions addBoardUser(RequestPostProcessor user, UUID boardId, Long userId, BoardUserRole role) throws Exception {
        BoardUser request = new BoardUser();
        request.setRole(role);
        return mockMvc.perform(post("/board/" + boardId + "/user/" + userId) //
                .with(csrf()) //
                .with(user) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    List<BoardUser> getBoardUsersOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardUsers(auth, boardId).andExpect(ok()), new TypeReference<List<BoardUser>>() {
        });
    }

    ResultActions getBoardUsers(RequestPostProcessor user, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/users") //
                .with(user));
    }

    List<String> getCategoriesOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getCategories(auth, boardId).andExpect(ok()), new TypeReference<List<String>>() {
        });
    }

    ResultActions getCategories(RequestPostProcessor user, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/categories") //
                .with(user));
    }

    BoardSplit addBoardSplitOk(RequestPostProcessor auth, UUID boardId, long userId, Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth, BigDecimal value1) throws Exception {
        return cs.jsonPayload(addBoardSplit(auth, boardId, userId, fromYear, fromMonth, toYear, toMonth, value1).andExpect(ok()), BoardSplit.class);
    }

    ResultActions addBoardSplit(RequestPostProcessor auth, UUID boardId, long userId, Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth, BigDecimal value1) throws Exception {
        BoardSplit request = new BoardSplit();
        request.setBoardId(boardId);
        request.setUserId(userId);
        request.setFromYear(fromYear);
        request.setFromMonth(fromMonth);
        request.setToYear(toYear);
        request.setToMonth(toMonth);
        request.setValue1(value1);

        return mockMvc.perform(post("/board/" + boardId + "/split") //
                .with(csrf()) //
                .with(auth) //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(cs.toJson(request)));
    }

    List<BoardSplit> getBoardSplitsOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardSplits(auth, boardId).andExpect(ok()), new TypeReference<List<BoardSplit>>() {
        });
    }

    ResultActions getBoardSplits(RequestPostProcessor user, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/splits") //
                .with(user) //
        );
    }

    List<MonthlyUserAnalysis> getBoardAnalysisMonthUserOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardAnalysisMonthUser(auth, boardId).andExpect(ok()), new TypeReference<List<MonthlyUserAnalysis>>() {
        });
    }

    ResultActions getBoardAnalysisMonthUser(RequestPostProcessor user, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/analysis/month-user") //
                .with(user) //
        );
    }

    UserAmount userAmount(long userId, String actual, String expected) {
        UserAmount obj = new UserAmount();
        obj.setUserId(userId);
        obj.setActual(new BigDecimal(actual));
        obj.setExpected(new BigDecimal(expected));
        return obj;
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

    void validateBoardSplits(List<BoardSplit> expected, List<BoardSplit> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach(e -> {
            BoardSplit actualEntry = actual.stream().filter(a -> a.getId().equals(e.getId())).findFirst().orElseThrow(() -> new NoSuchElementException("Missing entry for " + e));
            validateBoardSplit(e.getBoardId(), e.getUserId(), e.getFromYear(), e.getFromMonth(), e.getToYear(), e.getToMonth(), e.getValue1(), actualEntry);
        });
    }

    void validateBoardSplit(UUID boardId, long userId, Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth, BigDecimal value1, BoardSplit actual) {
        assertEquals(boardId, actual.getBoardId());
        assertEquals(userId, actual.getUserId());
        assertEquals(fromYear, actual.getFromYear());
        assertEquals(fromMonth, actual.getFromMonth());
        assertEquals(toYear, actual.getToYear());
        assertEquals(toMonth, actual.getToMonth());
        assertEquals(0, value1.compareTo(actual.getValue1()));
    }

    void validateMonthlyUserAnalysis(int year, int month, List<UserAmount> users, MonthlyUserAnalysis actual) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonth());
        assertEquals(users, actual.getUsers());
    }

    void validateUserAmounts(List<UserAmount> expected, List<UserAmount> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach(e -> {
            UserAmount actualEntry = actual.stream().filter(a -> a.getUserId() == e.getUserId()).findFirst().orElseThrow(() -> new NoSuchElementException("Missing entry for " + e));
            validateUserAmount(e, actualEntry);
        });
    }

    void validateUserAmount(UserAmount expected, UserAmount actual) {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(0, expected.getExpected().compareTo(actual.getExpected()));
        assertEquals(0, expected.getActual().compareTo(actual.getActual()));
    }
}