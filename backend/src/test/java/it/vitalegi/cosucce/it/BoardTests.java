package it.vitalegi.cosucce.it;


import it.vitalegi.cosucce.board.constant.BoardUserRole;
import it.vitalegi.cosucce.board.dto.Board;
import it.vitalegi.cosucce.board.dto.BoardEntry;
import it.vitalegi.cosucce.board.dto.BoardSplit;
import it.vitalegi.cosucce.board.dto.BoardUser;
import it.vitalegi.cosucce.board.dto.analysis.MonthlyAnalysis;
import it.vitalegi.cosucce.board.dto.analysis.MonthlyUserAnalysis;
import it.vitalegi.cosucce.board.repository.BoardRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_DELETE;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_EDIT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_ENTRY_EDIT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_ENTRY_IMPORT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_MANAGE_MEMBER;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_USER_ROLE_EDIT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_VIEW;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BoardTests extends RestResources {

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

    @DisplayName("GIVEN I am a member of the board WHEN I import new entries THEN I should receive an error")
    @Test
    public void test_addBoardEntries_member_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        addBoardEntries(auth2, board1.getId(), Collections.emptyList()).andExpect(error403());
    }

    @DisplayName("GIVEN I am not member of the board WHEN I import new entries THEN I should receive an error")
    @Test
    public void test_addBoardEntries_notMember_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        addBoardEntries(auth2, board1.getId(), Collections.emptyList()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I import new entries THEN the entries should be created")
    @Test
    public void test_addBoardEntries_owner_shouldCreateEntry() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        BoardEntry entry1 = boardEntry(null, user1.getId(), LocalDate.now(), new BigDecimal("123.12"), "A",
                "description1");
        BoardEntry entry2 = boardEntry(null, user2.getId(), LocalDate.now(), new BigDecimal("123.12"), "A",
                "description2");
        List<BoardEntry> entries = addBoardEntriesOk(auth1, board1.getId(), Arrays.asList(entry1, entry2));
        assertEquals(2, entries.size());
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

    @DisplayName("GIVEN I am the owner of the board WHEN I create a new invite THEN the invite should be created")
    @Test
    public void test_addBoardInvite_owner_shouldCreate() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        addBoardInviteOk(auth1, board.getId());
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
        deleteAllBoardSplits(auth1, board);

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

    @DisplayName("GIVEN I am a member of the board WHEN I delete the board THEN I should receive an error")
    @Test
    public void test_deleteBoard_member_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        deleteBoard(auth2, board1.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am not member of the board WHEN I delete the board THEN I should receive an error")
    @Test
    public void test_deleteBoard_notMember_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        deleteBoard(auth3, board1.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I delete the board THEN the board should be deleted")
    @Test
    public void test_deleteBoard_owner_shouldDeleteBoard() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        addBoardSplitOk(auth1, board1.getId(), user1.getId(), null, null, null, null, new BigDecimal("1"));
        addBoardEntry(auth1, board1.getId(), user1.getId(), LocalDate.of(2023, 1, 5), new BigDecimal("1"), "CAT1",
                null);

        deleteBoardOk(auth1, board1.getId());
    }

    // TODO split in multiple tests
    @DisplayName("GIVEN I am a user WHEN I retrieve board aggregated data THEN I should receive data")
    @Test
    public void test_getBoardAggregatedData() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());
        deleteAllBoardSplits(auth1, board);

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


    @DisplayName("GIVEN I am a member of the board WHEN I retrieve board analysis by month THEN the analysis should " + "be returned")
    @Test
    public void test_getBoardAnalysisMonth_member_shouldReturnData() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        addBoardSplitOk(auth1, board1.getId(), user1.getId(), null, null, null, null, new BigDecimal("0.5"));
        addBoardSplitOk(auth1, board1.getId(), user2.getId(), null, null, null, null, new BigDecimal("0.5"));
        addBoardEntry(auth1, board1.getId(), user1.getId(), LocalDate.of(2023, 1, 5), new BigDecimal("1"), "CAT1",
                null);
        addBoardEntry(auth1, board1.getId(), user1.getId(), LocalDate.of(2023, 2, 5), new BigDecimal("2"), "CAT1",
                null);

        List<MonthlyAnalysis> analysis = getBoardAnalysisMonthOk(auth2, board1.getId());
        assertEquals(2, analysis.size());
        validateMonthlyAnalysis(2023, 1, new BigDecimal("1"), analysis.get(0));
        validateMonthlyAnalysis(2023, 2, new BigDecimal("2"), analysis.get(1));
    }

    @DisplayName("GIVEN I am not member of the board  WHEN I retrieve board analysis by month THEN I should receive " + "an error")
    @Test
    public void test_getBoardAnalysisMonth_notMember_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        deleteBoard(auth3, board1.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I retrieve board analysis by month THEN the analysis should "
            + "be returned")
    @Test
    public void test_getBoardAnalysisMonth_owner_shouldReturnData() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        addBoardSplitOk(auth1, board1.getId(), user1.getId(), null, null, null, null, new BigDecimal("0.5"));
        addBoardSplitOk(auth1, board1.getId(), user2.getId(), null, null, null, null, new BigDecimal("0.5"));
        addBoardEntry(auth1, board1.getId(), user1.getId(), LocalDate.of(2023, 1, 5), new BigDecimal("1"), "CAT1",
                null);
        addBoardEntry(auth1, board1.getId(), user1.getId(), LocalDate.of(2023, 2, 5), new BigDecimal("2"), "CAT1",
                null);

        List<MonthlyAnalysis> analysis = getBoardAnalysisMonthOk(auth1, board1.getId());
        assertEquals(2, analysis.size());
        validateMonthlyAnalysis(2023, 1, new BigDecimal("1"), analysis.get(0));
        validateMonthlyAnalysis(2023, 2, new BigDecimal("2"), analysis.get(1));
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
        Board board1 = getBoardOk(auth2, board.getId());
        validateBoard("board1", board.getId(), board1);
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I retrieve the board THEN I should receive an error")
    @Test
    public void test_getBoard_notMember_shouldFail() throws Exception {
        Board board1 = addBoardOk(auth1, "board");
        getBoard(auth2, board1.getId()).andExpect(error403());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I retrieve the board THEN I should retrieve the board")
    @Test
    public void test_getBoard_owned_shouldReturnBoard() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        Board board = getBoardOk(auth1, board1.getId());
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

    @DisplayName("GIVEN I am a member of the board WHEN I get my grants THEN grants should be the ones for MEMBER")
    @Test
    public void test_getGrants_member_shouldRetrieveOnlyMemberGrants() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        List<BoardUserRole.BoardGrant> grants = getGrantsOk(auth2, board1.getId());
        assertArrayEquals(grants.toArray(), Arrays.asList(BOARD_VIEW, BOARD_ENTRY_EDIT).toArray());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I get my grants THEN grants should be empty")
    @Test
    public void test_getGrants_notMember_shouldNotRetrieveAnyGrant() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        List<BoardUserRole.BoardGrant> grants = getGrantsOk(auth3, board1.getId());
        assertEquals(0, grants.size());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I get my grants THEN grants should be the ones for OWNER")
    @Test
    public void test_getGrants_owner_shouldRetrieveOwnerGrants() throws Exception {
        Board board1 = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board1.getId());
        List<BoardUserRole.BoardGrant> grants = getGrantsOk(auth1, board1.getId());
        assertArrayEquals(grants.toArray(), Arrays.asList(BOARD_VIEW, BOARD_EDIT, BOARD_ENTRY_EDIT,
                                                          BOARD_USER_ROLE_EDIT, BOARD_MANAGE_MEMBER,
                                                          BOARD_ENTRY_IMPORT, BOARD_DELETE)
                                                  .toArray());
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

    @DisplayName("GIVEN I am a member of the board WHEN I update board's name THEN I should receive an error")
    @Test
    public void test_updateBoardName_member_shouldCreateEntry() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        updateBoardName(auth2, board.getId(), "new name").andExpect(error403());
        Board out = getBoardOk(auth1, board.getId());
        assertEquals("board1", out.getName());
    }

    @DisplayName("GIVEN I am not a member of the board WHEN I update board's name THEN I should receive an error")
    @Test
    public void test_updateBoardName_notMember_shouldFail() throws Exception {
        Board board = addBoardOk(auth1, "board1");

        updateBoardName(auth3, board.getId(), "new name").andExpect(error403());
        Board out = getBoardOk(auth1, board.getId());
        assertEquals("board1", out.getName());
    }

    @DisplayName("GIVEN I am the owner of the board WHEN I update board's name THEN the name should be updated")
    @Test
    public void test_updateBoardName_owner_shouldUpdate() throws Exception {
        Board board = addBoardOk(auth1, "board1");
        joinBoard(auth1, auth2, board.getId());

        updateBoardNameOk(auth1, board.getId(), "new name");
        Board out = getBoardOk(auth1, board.getId());
        assertEquals("new name", out.getName());
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
        deleteAllBoardSplits(auth1, board);

        BoardSplit split = addBoardSplitOk(auth1, board.getId(), user1.getId(), null, null, null, null,
                new BigDecimal("1"));

        updateBoardSplitOk(auth1, board.getId(), split.getId(), user1.getId(), 2022, 1, null, null, new BigDecimal("1"
        ));

        List<BoardSplit> splits = getBoardSplitsOk(auth1, board.getId());
        assertEquals(1, splits.size());
        validateBoardSplit(board.getId(), user1.getId(), 2022, 1, null, null, new BigDecimal("1"), splits.get(0));
    }

    @DisplayName("GIVEN I am the owner of the board and I created an invite WHEN an user that is not member uses the "
            + "invite THEN the user should join with role=MEMBER")
    @Test
    public void test_useBoardInvite_notMember_shouldJoin() throws Exception {
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

}