package it.vitalegi.budget.it;


import com.fasterxml.jackson.core.type.TypeReference;
import it.vitalegi.budget.board.analysis.dto.MonthlyUserAnalysis;
import it.vitalegi.budget.board.analysis.dto.UserAmount;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardInvite;
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

import static it.vitalegi.budget.it.HttpMonitor.monitor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @DisplayName("GIVEN I am a member of the board WHEN I add a new entry THEN the entry should be created")
    @Test
    public void test_addBoardEntry_member_shouldCreateEntry() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());

        BoardEntry entry = addBoardEntryOk(auth2, board1.getId(), user2.getId(), LocalDate.now(), new BigDecimal("100"
                + ".15"), "CATEGORY", "description");
        validateBoardEntry(board1.getId(), user2.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY",
                "description", entry);
    }

    @DisplayName("GIVEN I am not member of the board WHEN I add a new entry THEN I should receive an error")
    @Test
    public void test_addBoardEntry_notMember_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        addBoardEntry(auth2, board1.getId(), user2.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY",
                "description").andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I add a new entry THEN the entry should be created")
    @Test
    public void test_addBoardEntry_owner_shouldCreateEntry() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.now(),
                new BigDecimal("100" + ".15"), "CATEGORY", "description");
        validateBoardEntry(board.getId(), user1.getId(), LocalDate.now(), new BigDecimal("100.15"), "CATEGORY",
                "description", entry);
    }

    @DisplayName("GIVEN I am a member of the board WHEN I create a new invite THEN I should receive an error")
    @Test
    public void test_addBoardInvite_member_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        joinBoard(auth1, auth2, board.getId());

        addBoardInvite(auth2, board.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I create a new invite THEN I should receive an error")
    @Test
    public void test_addBoardInvite_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        addBoardInvite(auth2, board.getId()).andExpect(error403());
    }

    // TODO split in 3 tests
    @DisplayName("GIVEN I am the owner of the board WHEN I create a new invite THEN the invite should be created, " + "the user should be able to use it, the user should join the board with role=MEMBER")
    @Test
    public void test_addBoardInvite_owner_shouldAssign() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());
        List<BoardUser> users = getBoardUsersOk(auth1, board.getId());

        assertEquals(2, users.size());
        BoardUser u1 = users.stream().filter(u -> u.getUser().getId() == user1.getId()).findFirst().orElse(null);
        assertNotNull(u1);
        assertEquals(BoardUserRole.OWNER, u1.getRole());
        BoardUser u2 = users.stream().filter(u -> u.getUser().getId() == user2.getId()).findFirst().orElse(null);
        assertNotNull(u2);
        assertEquals(BoardUserRole.MEMBER, u2.getRole());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I add a new split THEN I should receive an error")
    @Test
    public void test_addBoardSplit_member_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        addBoardSplit(auth2, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("1")).andExpect(error403());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I add a new split configuration THEN I should receive " + "an" + " error")
    @Test
    public void test_addBoardSplit_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        addBoardSplit(auth2, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("1")).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I add a new split configuration THEN the split should be " + "created")
    @Test
    public void test_addBoardSplit_owner_shouldCreateSplit() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardSplit s1 = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null, new BigDecimal(
                "1"));
        BoardSplit s2 = addBoardSplitOk(auth1, board.getId(), user1.getId(), 2022, 1, 2022, 2, new BigDecimal("0.5"));
        BoardSplit s3 = addBoardSplitOk(auth1, board.getId(), user2.getId(), 2022, 1, null, null,
                new BigDecimal("0" + ".5"));
        BoardSplit s4 = addBoardSplitOk(auth1, board.getId(), user2.getId(), null, null, 2022, 2,
                new BigDecimal("0" + ".5"));
        List<BoardSplit> splits = getBoardSplitsOk(auth1, board.getId());
        validateBoardSplits(Arrays.asList(s1, s2, s3, s4), splits);
    }

    @DisplayName("GIVEN I am a user WHEN I create a board THEN the board should be created")
    @Test
    public void test_addBoard_shouldCreateBoard() throws Exception {
        addBoardOk(auth1, "board1");
    }

    @DisplayName("GIVEN I am a member of the board WHEN I delete an entry of another board THEN I should receive " +
            "an" + " error")
    @Test
    public void test_deleteBoardEntry_member_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        Board board2 = addBoardOk(auth1, "board2");

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5),
                new BigDecimal("123"), "CAT", null);
        deleteBoardEntry(auth1, board2.getId(), entry.getId()).andExpect(error500());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I delete an entry THEN the entry should be deleted")
    @Test
    public void test_deleteBoardEntry_member_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        joinBoard(auth1, auth2, board.getId());

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5),
                new BigDecimal("123"), "CAT", null);

        deleteBoardEntryOk(auth1, board.getId(), entry.getId());

        getBoardEntry(auth1, board.getId(), entry.getId()).andExpect(error500());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I delete a board entry THEN I should receive an error")
    @Test
    public void test_deleteBoardEntry_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5),
                new BigDecimal("123"), "CAT", null);
        deleteBoardEntry(auth2, board.getId(), entry.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I delete a split THEN I should receive an error")
    @Test
    public void test_deleteBoardSplit_member_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));

        deleteBoardSplit(auth2, board.getId(), split.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I delete a split configuration THEN I should receive an " + "error")
    @Test
    public void test_deleteBoardSplit_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));

        deleteBoardSplit(auth3, board.getId(), split.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I delete a split configuration THEN the split should be " +
            "deleted")
    @Test
    public void test_deleteBoardSplit_owner_shouldDeleteSplit() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));
        deleteBoardSplitOk(auth1, board.getId(), split.getId());
        List<BoardSplit> splits = getBoardSplitsOk(auth1, board.getId());
        assertTrue(splits.stream().noneMatch(s -> split.getId().equals(s.getId())));
    }

    // TODO split in multiple tests
    @DisplayName("GIVEN I am a user WHEN I retrieve board aggregated data THEN I should receive data")
    @Test
    public void test_getBoardAggregatedData() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        joinBoard(auth1, auth2, board.getId());

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2023, 1, 5), new BigDecimal("1"), "CAT1", null);
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2023, 2, 5), new BigDecimal("1"), "CAT1", null);
        addBoardEntry(auth1, board.getId(), user2.getId(), LocalDate.of(2023, 2, 5), new BigDecimal("1"), "CAT1", null);

        addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null, new BigDecimal("0.5"));
        addBoardSplitOk(auth1, board.getId(), user2.getId(), null, null, null, null, new BigDecimal("0.5"));

        List<MonthlyUserAnalysis> analysys = getBoardAnalysisMonthUserOk(auth1, board.getId());
        assertEquals(2, analysys.size());
        validateMonthlyUserAnalysis(2023, 1, Arrays.asList(userAmount(user1.getId(), "1", "0.5", "-0.5"),
                userAmount(user2.getId(), "0", "0.5", "0.5")), analysys.get(0));
        validateMonthlyUserAnalysis(2023, 2, Arrays.asList(userAmount(user1.getId(), "1", "1", "0"),
                userAmount(user2.getId(), "1", "1", "0")), analysys.get(1));
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve all board entries THEN I should retrieve all " +
            "entries of the board")
    @Test
    public void test_getBoardEntries_member_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5), new BigDecimal("123"), "CAT",
                null);
        addBoardEntry(auth2, board.getId(), user2.getId(), LocalDate.of(2022, 1, 10), new BigDecimal("456"), "CAT",
                null);
        List<BoardEntry> entries = getBoardEntriesOk(auth2, board.getId());
        assertEquals(2, entries.size());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I retrieve all board entries THEN I should receive an " + "error")
    @Test
    public void test_getBoardEntries_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5), new BigDecimal("123"), "CAT",
                null);
        getBoardEntries(auth3, board.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I retrieve all board entries THEN I should retrieve all " +
            "entries")
    @Test
    public void test_getBoardEntries_owner_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5), new BigDecimal("123"), "CAT",
                null);
        addBoardEntry(auth2, board.getId(), user2.getId(), LocalDate.of(2022, 1, 10), new BigDecimal("456"), "CAT",
                null);
        List<BoardEntry> entries = getBoardEntriesOk(auth1, board.getId());
        assertEquals(2, entries.size());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve one board entry with wrong IDs THEN I should " +
            "receive an error")
    @Test
    public void test_getBoardEntry_member_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        Board board2 = addBoardOk(auth1, "board2");

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5),
                new BigDecimal("123"), "CAT", null);
        getBoardEntry(auth1, board2.getId(), entry.getId()).andExpect(error500());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve one board entry THEN I should retrieve the entry")
    @Test
    public void test_getBoardEntry_member_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5),
                new BigDecimal("123"), "CAT", null);
        BoardEntry actual = getBoardEntryOk(auth2, board.getId(), entry.getId());
        validateBoardEntry(entry.getBoardId(), entry.getOwnerId(), entry.getDate(), entry.getAmount(),
                entry.getCategory(), entry.getDescription(), actual);
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I retrieve one board entry THEN I should receive an error")
    @Test
    public void test_getBoardEntry_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5),
                new BigDecimal("123"), "CAT", null);
        getBoardEntry(auth2, board.getId(), entry.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve board users of the board THEN I should retrieve " + "the board users")
    @Test
    public void test_getBoardUsers_member_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        List<BoardUser> users = getBoardUsersOk(auth2, board.getId());
        assertEquals(2, users.size());
        assertEquals(BoardUserRole.OWNER, users.stream().filter(u -> u.getUser().getId() == user1.getId()).findFirst()
                                               .orElseThrow(() -> new NoSuchElementException("User1 not found"))
                                               .getRole());
        assertEquals(BoardUserRole.MEMBER, users.stream().filter(u -> u.getUser().getId() == user2.getId()).findFirst()
                                                .orElseThrow(() -> new NoSuchElementException("User2 not found"))
                                                .getRole());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I retrieve board users of the board THEN I should " +
            "retrieve an error")
    @Test
    public void test_getBoardUsers_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        getBoardUsers(auth2, board.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I retrieve board users of the board THEN I should retrieve " + "the board users")
    @Test
    public void test_getBoardUsers_owner_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        List<BoardUser> users = getBoardUsersOk(auth1, board.getId());
        assertEquals(2, users.size());
        assertEquals(BoardUserRole.OWNER, users.stream().filter(u -> u.getUser().getId() == user1.getId()).findFirst()
                                               .orElseThrow(() -> new NoSuchElementException("User1 not found"))
                                               .getRole());
        assertEquals(BoardUserRole.MEMBER, users.stream().filter(u -> u.getUser().getId() == user2.getId()).findFirst()
                                                .orElseThrow(() -> new NoSuchElementException("User2 not found"))
                                                .getRole());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve the board THEN the board should be retrieved")
    @Test
    public void test_getBoard_member_shouldReturnBoard() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());
        Board board1 = getBoardOk(auth2, board.getId().toString());
        validateBoard("board1", board.getId(), board1);
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I retrieve the board THEN I should receive an error")
    @Test
    public void test_getBoard_notMember_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board");
        getBoard(auth2, board1.getId().toString()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I retrieve the board THEN I should retrieve the board")
    @Test
    public void test_getBoard_owned_shouldReturnBoard() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        Board board = getBoardOk(auth1, board1.getId().toString());
        validateBoard("board1", board1.getId(), board);
    }

    @DisplayName("GIVEN I am not logged in WHEN I retrieve the board THEN I should receive an unauthorized error")
    @Test
    public void test_getBoard_unknown_shouldReturnBoard() throws Exception {
        mockMvc.perform(get("/user")).andExpect(status().isUnauthorized());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve the boards THEN I should receive only the boards " + "I'm a member of")
    @Test
    public void test_getBoards_shouldReturnBoards() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        log.info("user1 is owner of board1");

        Board board2 = addBoardOk(auth2, "board2");
        joinBoard(auth2, auth1, board2.getId());
        log.info("user1 is member of board2");

        Board board3 = addBoardOk(auth3, "board3");
        log.info("user1 is not in board3");

        List<Board> boards = getBoardsOk(auth1);
        assertEquals(2, boards.size());
        Board out1 = boards.stream().filter(b -> b.getId().equals(board1.getId())).findFirst()
                           .orElseThrow(() -> new NoSuchElementException("Missing board1"));
        Board out2 = boards.stream().filter(b -> b.getId().equals(board2.getId())).findFirst()
                           .orElseThrow(() -> new NoSuchElementException("Missing board2"));
        validateBoard(board1.getName(), board1.getId(), out1);
        validateBoard(board2.getName(), board2.getId(), out2);
        assertFalse(boards.stream().anyMatch(b -> b.getId().equals(board3.getId())));
    }

    @DisplayName("GIVEN I am a member of the board WHEN I retrieve the categories THEN I should retrieve all " +
            "categories")
    @Test
    public void test_getCategories_shouldWork() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 5), new BigDecimal("123"), "CAT1",
                null);
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 10), new BigDecimal("456"), "CAT2",
                null);
        addBoardEntry(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 10), new BigDecimal("456"), "CAT2",
                null);
        List<String> entries = getCategoriesOk(auth1, board.getId());
        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(c -> c.equals("CAT1")));
        assertTrue(entries.stream().anyMatch(c -> c.equals("CAT2")));
    }

    @DisplayName("GIVEN I am a member of the board WHEN I update a board entry THEN the entry should be updated")
    @Test
    public void test_updateBoardEntry_member_shouldCreateEntry() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 2),
                new BigDecimal("100.15"), "CATEGORY", "description");
        validateBoardEntry(board.getId(), user1.getId(), LocalDate.of(2022, 1, 2), new BigDecimal("100.15"),
                "CATEGORY", "description", entry);

        BoardEntry updated = updateBoardEntryOk(auth2, board.getId(), entry.getId(), user2.getId(), LocalDate.of(2023
                , 3, 4), new BigDecimal("10"), "NEW CATEGORY", "new description");
        validateBoardEntry(board.getId(), user2.getId(), LocalDate.of(2023, 3, 4), new BigDecimal("10"), "NEW " +
                "CATEGORY", "new description", updated);
        assertEquals(entry.getId(), updated.getId());

        BoardEntry updated2 = getBoardEntriesOk(auth2, board.getId()).get(0);
        validateBoardEntry(board.getId(), user2.getId(), LocalDate.of(2023, 3, 4), new BigDecimal("10"), "NEW " +
                "CATEGORY", "new description", updated2);
        assertEquals(entry.getId(), updated2.getId());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I update a board entry THEN I should receive an error")
    @Test
    public void test_updateBoardEntry_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 2),
                new BigDecimal("100.15"), "CATEGORY", "description");
        validateBoardEntry(board.getId(), user1.getId(), LocalDate.of(2022, 1, 2), new BigDecimal("100.15"),
                "CATEGORY", "description", entry);

        log.info("A user that is not part of the board cannot edit it");
        updateBoardEntry(auth2, board.getId(), entry.getId(), user1.getId(), LocalDate.of(2023, 3, 4),
                new BigDecimal("10"), "NEW CATEGORY", "new description").andExpect(error403());

        log.info("A user that is not part of the board cannot be assigned to its resources");
        updateBoardEntry(auth1, board.getId(), entry.getId(), user2.getId(), LocalDate.of(2023, 3, 4),
                new BigDecimal("10"), "NEW CATEGORY", "new description").andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I update a board entry THEN the entry should be updated")
    @Test
    public void test_updateBoardEntry_owner_shouldUpdate() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardEntry entry = addBoardEntryOk(auth1, board.getId(), user1.getId(), LocalDate.of(2022, 1, 2),
                new BigDecimal("100.15"), "CATEGORY", "description");
        validateBoardEntry(board.getId(), user1.getId(), LocalDate.of(2022, 1, 2), new BigDecimal("100.15"),
                "CATEGORY", "description", entry);

        BoardEntry updated = updateBoardEntryOk(auth1, board.getId(), entry.getId(), user2.getId(), LocalDate.of(2023
                , 3, 4), new BigDecimal("10"), "NEW CATEGORY", "new description");
        validateBoardEntry(board.getId(), user2.getId(), LocalDate.of(2023, 3, 4), new BigDecimal("10"), "NEW " +
                "CATEGORY", "new description", updated);
        assertEquals(entry.getId(), updated.getId());

        BoardEntry updated2 = getBoardEntriesOk(auth1, board.getId()).get(0);
        validateBoardEntry(board.getId(), user2.getId(), LocalDate.of(2023, 3, 4), new BigDecimal("10"), "NEW " +
                "CATEGORY", "new description", updated2);
        assertEquals(entry.getId(), updated2.getId());
    }

    @DisplayName("GIVEN I am a member of the board WHEN I update a split THEN I should receive an error")
    @Test
    public void test_updateBoardSplit_member_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));

        updateBoardSplit(auth2, board.getId(), split.getId(), user1.getId(), 2022, 1, null, null,
                new BigDecimal("1")).andExpect(error403());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I update a split configuration THEN I should receive an " + "error")
    @Test
    public void test_updateBoardSplit_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));

        updateBoardSplit(auth3, board.getId(), split.getId(), user1.getId(), 2022, 1, null, null,
                new BigDecimal("1")).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I update a split configuration THEN the split should be " +
            "updated")
    @Test
    public void test_updateBoardSplit_owner_shouldUpdateSplit() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));

        updateBoardSplitOk(auth1, board.getId(), split.getId(), user1.getId(), 2022, 1, null, null,
                new BigDecimal("1"));

        List<BoardSplit> splits = getBoardSplitsOk(auth1, board.getId());
        assertEquals(1, splits.size());
        validateBoardSplit(board.getId(), user1.getId(), 2022, 1, null, null, new BigDecimal("1"), splits.get(0));
    }

    private ResultActions access(RequestPostProcessor auth) throws Exception {
        return mockMvc.perform(get("/user").with(auth)).andDo(monitor());
    }

    private User accessOk(RequestPostProcessor auth, String uid) throws Exception {
        User out = cs.jsonPayload(access(auth).andExpect(ok()), User.class);
        validateUser(null, uid, out);
        return out;
    }

    private ResultActions addBoard(RequestPostProcessor auth, String name) throws Exception {
        Board request = new Board();
        request.setName(name);

        return mockMvc.perform(post("/board") //
                                              .with(csrf()) //
                                              .with(auth) //
                                              .contentType(MediaType.APPLICATION_JSON) //
                                              .content(cs.toJson(request))) //
                      .andDo(monitor());
    }

    private ResultActions addBoardEntry(RequestPostProcessor auth, UUID boardId, Long ownerId, LocalDate date,
                                        BigDecimal amount, String category, String description) throws Exception {
        BoardEntry request = new BoardEntry();
        request.setBoardId(boardId);
        request.setOwnerId(ownerId);
        request.setAmount(amount);
        request.setDescription(description);
        request.setCategory(category);
        request.setDate(date);

        return mockMvc.perform(post("/board/" + boardId + "/entry").with(csrf()).with(auth)
                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                   .content(cs.toJson(request))).andDo(monitor());
    }

    private BoardEntry addBoardEntryOk(RequestPostProcessor auth, UUID boardId, Long ownerId, LocalDate date,
                                       BigDecimal amount, String category, String description) throws Exception {
        return cs.jsonPayload(addBoardEntry(auth, boardId, ownerId, date, amount, category, description).andExpect(ok()), BoardEntry.class);
    }

    private ResultActions addBoardInvite(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(post("/board/" + boardId + "/invite").with(csrf()).with(auth)
                                                                    .contentType(MediaType.APPLICATION_JSON))
                      .andDo(monitor());
    }

    private BoardInvite addBoardInviteOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayload(addBoardInvite(auth, boardId).andExpect(ok()), BoardInvite.class);
    }

    private Board addBoardOk(RequestPostProcessor auth, String boardName) throws Exception {
        Board board = cs.jsonPayload(addBoard(auth, boardName).andExpect(ok()), Board.class);
        validateBoard(boardName, null, board);
        return board;
    }

    private ResultActions addBoardSplit(RequestPostProcessor auth, UUID boardId, long userId, Integer fromYear,
                                        Integer fromMonth, Integer toYear, Integer toMonth, BigDecimal value1) throws Exception {
        BoardSplit request = new BoardSplit();
        request.setBoardId(boardId);
        request.setUserId(userId);
        request.setFromYear(fromYear);
        request.setFromMonth(fromMonth);
        request.setToYear(toYear);
        request.setToMonth(toMonth);
        request.setValue1(value1);

        return mockMvc.perform(post("/board/" + boardId + "/split").with(csrf()).with(auth)
                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                   .content(cs.toJson(request))).andDo(monitor());
    }

    private BoardSplit addBoardSplitOk(RequestPostProcessor auth, UUID boardId, long userId, Integer fromYear,
                                       Integer fromMonth, Integer toYear, Integer toMonth, BigDecimal value1) throws Exception {
        return cs.jsonPayload(addBoardSplit(auth, boardId, userId, fromYear, fromMonth, toYear, toMonth, value1).andExpect(ok()), BoardSplit.class);
    }

    private ResultActions deleteBoardEntry(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        return mockMvc.perform(delete("/board/" + boardId + "/entry/" + boardEntryId).with(csrf()).with(auth))
                      .andDo(monitor());
    }

    private void deleteBoardEntryOk(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        deleteBoardEntry(auth, boardId, boardEntryId).andExpect(ok());
    }

    private ResultActions deleteBoardSplit(RequestPostProcessor auth, UUID boardId, UUID splitId) throws Exception {
        return mockMvc.perform(delete("/board/" + boardId + "/split/" + splitId).with(csrf()).with(auth)
                                                                                .contentType(MediaType.APPLICATION_JSON))
                      .andDo(monitor());
    }

    private void deleteBoardSplitOk(RequestPostProcessor auth, UUID boardId, UUID splitId) throws Exception {
        deleteBoardSplit(auth, boardId, splitId).andExpect(ok());
    }

    private ResultMatcher error403() {
        return status().isForbidden();
    }

    private ResultMatcher error500() {
        return status().isInternalServerError();
    }

    private ResultActions getBoard(RequestPostProcessor auth, String boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId).with(auth))//
                      .andDo(monitor());
    }

    private ResultActions getBoardAnalysisMonthUser(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/analysis/month-user").with(auth)).andDo(monitor());
    }

    private List<MonthlyUserAnalysis> getBoardAnalysisMonthUserOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardAnalysisMonthUser(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    private ResultActions getBoardEntries(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/entries").with(auth)).andDo(monitor());
    }

    private List<BoardEntry> getBoardEntriesOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardEntries(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    private ResultActions getBoardEntry(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/entry/" + boardEntryId).with(auth)).andDo(monitor());
    }

    private BoardEntry getBoardEntryOk(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        return cs.jsonPayload(getBoardEntry(auth, boardId, boardEntryId).andExpect(ok()), BoardEntry.class);
    }

    private Board getBoardOk(RequestPostProcessor auth, String boardId) throws Exception {
        return cs.jsonPayload(getBoard(auth, boardId).andExpect(ok()), Board.class);
    }

    private ResultActions getBoardSplits(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/splits").with(auth)).andDo(monitor());
    }

    private List<BoardSplit> getBoardSplitsOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardSplits(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    private ResultActions getBoardUsers(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/users").with(auth)).andDo(monitor());
    }

    private List<BoardUser> getBoardUsersOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardUsers(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    private ResultActions getBoards(RequestPostProcessor auth) throws Exception {
        return mockMvc.perform(get("/board").with(auth))//
                      .andDo(monitor());
    }

    private List<Board> getBoardsOk(RequestPostProcessor auth) throws Exception {
        return cs.jsonPayloadList(getBoards(auth).andExpect(ok()), new TypeReference<>() {
        });
    }

    private ResultActions getCategories(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/categories").with(auth)).andDo(monitor());
    }

    private List<String> getCategoriesOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getCategories(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    private void joinBoard(RequestPostProcessor ownerAuth, RequestPostProcessor joinerAuth, UUID boardId) throws Exception {
        BoardInvite invite = addBoardInviteOk(ownerAuth, boardId);
        useBoardInviteOk(joinerAuth, boardId, invite.getId());
    }

    ResultMatcher ok() {
        return status().isOk();
    }

    private ResultActions updateBoardEntry(RequestPostProcessor auth, UUID boardId, UUID boardEntryId, Long ownerId,
                                           LocalDate date, BigDecimal amount, String category, String description) throws Exception {
        BoardEntry request = new BoardEntry();
        request.setId(boardEntryId);
        request.setBoardId(boardId);
        request.setOwnerId(ownerId);
        request.setAmount(amount);
        request.setDescription(description);
        request.setCategory(category);
        request.setDate(date);

        return mockMvc.perform(put("/board/" + boardId + "/entry").with(csrf()).with(auth)
                                                                  .contentType(MediaType.APPLICATION_JSON)
                                                                  .content(cs.toJson(request))).andDo(monitor());
    }

    private BoardEntry updateBoardEntryOk(RequestPostProcessor auth, UUID boardId, UUID boardEntryId, Long ownerId,
                                          LocalDate date, BigDecimal amount, String category, String description) throws Exception {
        return cs.jsonPayload(updateBoardEntry(auth, boardId, boardEntryId, ownerId, date, amount, category,
                description).andExpect(ok()), BoardEntry.class);
    }

    private ResultActions updateBoardSplit(RequestPostProcessor auth, UUID boardId, UUID boardSplitId, long userId,
                                           Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth,
                                           BigDecimal value1) throws Exception {
        BoardSplit request = new BoardSplit();
        request.setId(boardSplitId);
        request.setBoardId(boardId);
        request.setUserId(userId);
        request.setFromYear(fromYear);
        request.setFromMonth(fromMonth);
        request.setToYear(toYear);
        request.setToMonth(toMonth);
        request.setValue1(value1);

        return mockMvc.perform(put("/board/" + boardId + "/split").with(csrf()).with(auth)
                                                                  .contentType(MediaType.APPLICATION_JSON)
                                                                  .content(cs.toJson(request))).andDo(monitor());
    }

    private BoardSplit updateBoardSplitOk(RequestPostProcessor auth, UUID boardId, UUID boardSplitId, long userId,
                                          Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth,
                                          BigDecimal value1) throws Exception {
        return cs.jsonPayload(updateBoardSplit(auth, boardId, boardSplitId, userId, fromYear, fromMonth, toYear,
                toMonth, value1).andExpect(ok()), BoardSplit.class);
    }

    private ResultActions useBoardInvite(RequestPostProcessor auth, UUID boardId, UUID inviteId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/invite/" + inviteId).with(csrf()).with(auth))
                      .andDo(monitor());
    }

    private void useBoardInviteOk(RequestPostProcessor auth, UUID boardId, UUID inviteId) throws Exception {
        useBoardInvite(auth, boardId, inviteId).andExpect(ok());
    }

    private UserAmount userAmount(long userId, String actual, String expected, String cumulatedCredit) {
        UserAmount obj = new UserAmount();
        obj.setUserId(userId);
        obj.setActual(new BigDecimal(actual));
        obj.setExpected(new BigDecimal(expected));
        obj.setCumulatedCredit(new BigDecimal(cumulatedCredit));
        return obj;
    }

    private void validateBoard(String name, UUID boardId, Board actual) {
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

    private void validateBoardEntry(UUID boardId, Long ownerId, LocalDate date, BigDecimal amount, String category,
                                    String description, BoardEntry actual) {
        assertEquals(boardId, actual.getBoardId());
        assertEquals(ownerId, actual.getOwnerId());
        assertEquals(date, actual.getDate());
        assertEquals(0, amount.compareTo(actual.getAmount()));
        assertEquals(category, actual.getCategory());
        assertEquals(description, actual.getDescription());
    }

    private void validateBoardSplit(UUID boardId, long userId, Integer fromYear, Integer fromMonth, Integer toYear,
                                    Integer toMonth, BigDecimal value1, BoardSplit actual) {
        assertEquals(boardId, actual.getBoardId());
        assertEquals(userId, actual.getUserId());
        assertEquals(fromYear, actual.getFromYear());
        assertEquals(fromMonth, actual.getFromMonth());
        assertEquals(toYear, actual.getToYear());
        assertEquals(toMonth, actual.getToMonth());
        assertEquals(0, value1.compareTo(actual.getValue1()));
    }

    private void validateBoardSplits(List<BoardSplit> expected, List<BoardSplit> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach(e -> {
            BoardSplit actualEntry = actual.stream().filter(a -> a.getId().equals(e.getId())).findFirst()
                                           .orElseThrow(() -> new NoSuchElementException("Missing entry for " + e));
            validateBoardSplit(e.getBoardId(), e.getUserId(), e.getFromYear(), e.getFromMonth(), e.getToYear(),
                    e.getToMonth(), e.getValue1(), actualEntry);
        });
    }

    private void validateMonthlyUserAnalysis(int year, int month, List<UserAmount> users, MonthlyUserAnalysis actual) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonth());
        assertEquals(users, actual.getUsers());
    }

    private void validateUser(Long id, String uid, User actual) {
        assertEquals(uid, actual.getUid());
        if (id != null) {
            assertEquals(id, actual.getId());
        }
        String username = mockAuth.username(uid);
        assertEquals(username, actual.getUsername());
    }
}