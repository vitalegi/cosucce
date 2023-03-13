package it.vitalegi.budget.it;

import com.fasterxml.jackson.core.type.TypeReference;
import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.dto.AddBoard;
import it.vitalegi.budget.board.dto.AddBoardEntries;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardInvite;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.dto.analysis.MonthlyAnalysis;
import it.vitalegi.budget.board.dto.analysis.MonthlyUserAnalysis;
import it.vitalegi.budget.board.dto.analysis.UserAmount;
import it.vitalegi.budget.it.framework.CallService;
import it.vitalegi.budget.it.framework.MockAuth;
import it.vitalegi.budget.spando.constant.SpandoDay;
import it.vitalegi.budget.spando.dto.SpandoDays;
import it.vitalegi.budget.spando.dto.SpandoEntry;
import it.vitalegi.budget.user.constant.OtpStatus;
import it.vitalegi.budget.user.dto.OtpResponse;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.dto.UserOtp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static it.vitalegi.budget.it.framework.HttpMonitor.monitor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestResources {

    @Autowired
    CallService cs;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MockAuth mockAuth;

    protected ResultActions access(RequestPostProcessor auth) throws Exception {
        return mockMvc.perform(get("/user").with(auth)).andDo(monitor());
    }

    protected User accessOk(RequestPostProcessor auth, String uid) throws Exception {
        User out = cs.jsonPayload(access(auth).andExpect(ok()), User.class);
        validateUser(null, uid, out);
        return out;
    }

    protected ResultActions addBoard(RequestPostProcessor auth, String name) throws Exception {
        Board request = new Board();
        request.setName(name);

        return mockMvc.perform(post("/board").with(csrf()).with(auth).contentType(MediaType.APPLICATION_JSON)
                                             .content(cs.toJson(request))).andDo(monitor());
    }

    protected ResultActions addBoardEntries(RequestPostProcessor auth, UUID boardId, List<BoardEntry> entries) throws Exception {
        AddBoardEntries request = new AddBoardEntries();
        request.setEntries(entries);
        return mockMvc.perform(post("/board/" + boardId + "/entries").with(csrf()).with(auth)
                                                                     .contentType(MediaType.APPLICATION_JSON)
                                                                     .content(cs.toJson(request))).andDo(monitor());
    }

    protected List<BoardEntry> addBoardEntriesOk(RequestPostProcessor auth, UUID boardId, List<BoardEntry> entries) throws Exception {
        return cs.jsonPayloadList(addBoardEntries(auth, boardId, entries).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions addBoardEntry(RequestPostProcessor auth, UUID boardId, Long ownerId, LocalDate date,
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

    protected BoardEntry addBoardEntryOk(RequestPostProcessor auth, UUID boardId, Long ownerId, LocalDate date,
                                         BigDecimal amount, String category, String description) throws Exception {
        return cs.jsonPayload(addBoardEntry(auth, boardId, ownerId, date, amount, category, description).andExpect(ok()), BoardEntry.class);
    }

    protected ResultActions addBoardInvite(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(post("/board/" + boardId + "/invite").with(csrf()).with(auth)
                                                                    .contentType(MediaType.APPLICATION_JSON))
                      .andDo(monitor());
    }

    protected BoardInvite addBoardInviteOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayload(addBoardInvite(auth, boardId).andExpect(ok()), BoardInvite.class);
    }

    protected Board addBoardOk(RequestPostProcessor auth, String boardName) throws Exception {
        Board board = cs.jsonPayload(addBoard(auth, boardName).andExpect(ok()), Board.class);
        validateBoard(boardName, null, board);
        return board;
    }

    protected ResultActions addBoardSplit(RequestPostProcessor auth, UUID boardId, long userId, Integer fromYear,
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

    protected BoardSplit addBoardSplitOk(RequestPostProcessor auth, UUID boardId, long userId, Integer fromYear,
                                         Integer fromMonth, Integer toYear, Integer toMonth, BigDecimal value1) throws Exception {
        return cs.jsonPayload(addBoardSplit(auth, boardId, userId, fromYear, fromMonth, toYear, toMonth, value1).andExpect(ok()), BoardSplit.class);
    }

    protected ResultActions addSpandoEntry(RequestPostProcessor auth, LocalDate date) throws Exception {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return mockMvc.perform(post("/spando/" + formattedDate).with(csrf()).with(auth)
                                                               .contentType(MediaType.APPLICATION_JSON).content("{}"))
                      .andDo(monitor());
    }

    protected SpandoEntry addSpandoEntryOk(RequestPostProcessor auth, LocalDate date) throws Exception {
        return cs.jsonPayload(addSpandoEntry(auth, date).andExpect(ok()), SpandoEntry.class);
    }

    protected ResultActions addUserOtp(RequestPostProcessor auth) throws Exception {

        return mockMvc.perform(put("/user/otp").with(csrf()).with(auth).contentType(MediaType.APPLICATION_JSON)
                                               .content("{}")).andDo(monitor());
    }

    protected UserOtp addUserOtpOk(RequestPostProcessor auth) throws Exception {
        UserOtp otp = cs.jsonPayload(addUserOtp(auth).andExpect(ok()), UserOtp.class);
        validateUserOtp(null, OtpStatus.ACTIVE, otp);
        return otp;
    }

    protected BoardEntry boardEntry(UUID boardId, Long ownerId, LocalDate date, BigDecimal amount, String category,
                                    String description) {

        BoardEntry entry = new BoardEntry();
        entry.setBoardId(boardId);
        entry.setOwnerId(ownerId);
        entry.setAmount(amount);
        entry.setDescription(description);
        entry.setCategory(category);
        entry.setDate(date);
        return entry;
    }

    protected void deleteAllBoardSplits(RequestPostProcessor auth, Board board) throws Exception {
        List<BoardSplit> splits = getBoardSplitsOk(auth, board.getId());
        for (BoardSplit split : splits) {
            deleteBoardSplitOk(auth, board.getId(), split.getId());
        }
    }

    protected ResultActions deleteBoard(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(delete("/board/" + boardId).with(csrf()).with(auth)).andDo(monitor());
    }

    protected ResultActions deleteBoardEntry(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        return mockMvc.perform(delete("/board/" + boardId + "/entry/" + boardEntryId).with(csrf()).with(auth))
                      .andDo(monitor());
    }

    protected void deleteBoardEntryOk(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        deleteBoardEntry(auth, boardId, boardEntryId).andExpect(ok());
    }

    protected void deleteBoardOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        deleteBoard(auth, boardId).andExpect(ok());
    }

    protected ResultActions deleteBoardSplit(RequestPostProcessor auth, UUID boardId, UUID splitId) throws Exception {
        return mockMvc.perform(delete("/board/" + boardId + "/split/" + splitId).with(csrf()).with(auth)
                                                                                .contentType(MediaType.APPLICATION_JSON))
                      .andDo(monitor());
    }

    protected void deleteBoardSplitOk(RequestPostProcessor auth, UUID boardId, UUID splitId) throws Exception {
        deleteBoardSplit(auth, boardId, splitId).andExpect(ok());
    }

    protected ResultActions deleteSpandoEntry(RequestPostProcessor auth, LocalDate date) throws Exception {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return mockMvc.perform(delete("/spando/" + formattedDate).with(csrf()).with(auth)
                                                                 .contentType(MediaType.APPLICATION_JSON))
                      .andDo(monitor());
    }

    protected void deleteSpandoEntryOk(RequestPostProcessor auth, LocalDate date) throws Exception {
        deleteSpandoEntry(auth, date).andExpect(ok());
    }

    protected ResultMatcher error403() {
        return status().isForbidden();
    }

    protected ResultMatcher error500() {
        return status().isInternalServerError();
    }

    protected ResultActions getBoard(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId).with(auth))//
                      .andDo(monitor());
    }

    protected ResultActions getBoardAnalysisMonth(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/analysis/month").with(auth)).andDo(monitor());
    }

    protected List<MonthlyAnalysis> getBoardAnalysisMonthOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardAnalysisMonth(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getBoardAnalysisMonthUser(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/analysis/month-user").with(auth)).andDo(monitor());
    }

    protected List<MonthlyUserAnalysis> getBoardAnalysisMonthUserOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardAnalysisMonthUser(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getBoardEntries(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/entries").with(auth)).andDo(monitor());
    }

    protected List<BoardEntry> getBoardEntriesOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardEntries(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getBoardEntry(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/entry/" + boardEntryId).with(auth)).andDo(monitor());
    }

    protected BoardEntry getBoardEntryOk(RequestPostProcessor auth, UUID boardId, UUID boardEntryId) throws Exception {
        return cs.jsonPayload(getBoardEntry(auth, boardId, boardEntryId).andExpect(ok()), BoardEntry.class);
    }

    protected Board getBoardOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayload(getBoard(auth, boardId).andExpect(ok()), Board.class);
    }

    protected ResultActions getBoardSplits(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/splits").with(auth)).andDo(monitor());
    }

    protected List<BoardSplit> getBoardSplitsOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardSplits(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getBoardUsers(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/users").with(auth)).andDo(monitor());
    }

    protected List<BoardUser> getBoardUsersOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getBoardUsers(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getBoards(RequestPostProcessor auth) throws Exception {
        return mockMvc.perform(get("/board").with(auth))//
                      .andDo(monitor());
    }

    protected List<Board> getBoardsOk(RequestPostProcessor auth) throws Exception {
        return cs.jsonPayloadList(getBoards(auth).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getCategories(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/categories").with(auth)).andDo(monitor());
    }

    protected List<String> getCategoriesOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getCategories(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getGrants(RequestPostProcessor auth, UUID boardId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/grants").with(auth))//
                      .andDo(monitor());
    }

    protected List<BoardGrant> getGrantsOk(RequestPostProcessor auth, UUID boardId) throws Exception {
        return cs.jsonPayloadList(getGrants(auth, boardId).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected ResultActions getSpandos(RequestPostProcessor auth) throws Exception {
        return mockMvc.perform(get("/spando").with(auth)).andDo(monitor());
    }

    protected List<SpandoDays> getSpandosOk(RequestPostProcessor auth) throws Exception {
        return cs.jsonPayloadList(getSpandos(auth).andExpect(ok()), new TypeReference<>() {
        });
    }

    protected User getUserOk(RequestPostProcessor auth) throws Exception {
        return cs.jsonPayload(access(auth).andExpect(ok()), User.class);
    }

    protected void joinBoard(RequestPostProcessor ownerAuth, RequestPostProcessor joinerAuth, UUID boardId) throws Exception {
        BoardInvite invite = addBoardInviteOk(ownerAuth, boardId);
        useBoardInviteOk(joinerAuth, boardId, invite.getId());
    }

    protected ResultMatcher ok() {
        return status().isOk();
    }

    protected ResultActions updateBoardEntry(RequestPostProcessor auth, UUID boardId, UUID boardEntryId, Long ownerId
            , LocalDate date, BigDecimal amount, String category, String description) throws Exception {
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

    protected BoardEntry updateBoardEntryOk(RequestPostProcessor auth, UUID boardId, UUID boardEntryId, Long ownerId,
                                            LocalDate date, BigDecimal amount, String category, String description) throws Exception {
        return cs.jsonPayload(updateBoardEntry(auth, boardId, boardEntryId, ownerId, date, amount, category,
                description).andExpect(ok()), BoardEntry.class);
    }

    protected ResultActions updateBoardName(RequestPostProcessor auth, UUID boardId, String name) throws Exception {
        AddBoard request = new AddBoard();
        request.setName(name);

        return mockMvc.perform(put("/board/" + boardId).with(csrf()).with(auth).contentType(MediaType.APPLICATION_JSON)
                                                       .content(cs.toJson(request))).andDo(monitor());
    }

    protected Board updateBoardNameOk(RequestPostProcessor auth, UUID boardId, String name) throws Exception {
        return cs.jsonPayload(updateBoardName(auth, boardId, name).andExpect(ok()), Board.class);
    }

    protected ResultActions updateBoardSplit(RequestPostProcessor auth, UUID boardId, UUID boardSplitId, long userId,
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

    protected BoardSplit updateBoardSplitOk(RequestPostProcessor auth, UUID boardId, UUID boardSplitId, long userId,
                                            Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth,
                                            BigDecimal value1) throws Exception {
        return cs.jsonPayload(updateBoardSplit(auth, boardId, boardSplitId, userId, fromYear, fromMonth, toYear,
                toMonth, value1).andExpect(ok()), BoardSplit.class);
    }

    protected ResultActions updateUsername(RequestPostProcessor auth, String username) throws Exception {
        User request = new User();
        request.setUsername(username);
        return mockMvc.perform(put("/user/").with(csrf()).with(auth).contentType(MediaType.APPLICATION_JSON)
                                            .content(cs.toJson(request))).andDo(monitor());
    }

    protected User updateUsernameOk(RequestPostProcessor auth, String username) throws Exception {
        return cs.jsonPayload(updateUsername(auth, username).andExpect(ok()), User.class);
    }

    protected ResultActions useBoardInvite(RequestPostProcessor auth, UUID boardId, UUID inviteId) throws Exception {
        return mockMvc.perform(get("/board/" + boardId + "/invite/" + inviteId).with(csrf()).with(auth))
                      .andDo(monitor());
    }

    protected void useBoardInviteOk(RequestPostProcessor auth, UUID boardId, UUID inviteId) throws Exception {
        useBoardInvite(auth, boardId, inviteId).andExpect(ok());
    }

    protected ResultActions useOtp(RequestPostProcessor auth, String otp) throws Exception {

        return mockMvc.perform(post("/user/otp/" + otp).with(csrf()).with(auth).contentType(MediaType.APPLICATION_JSON)
                                                       .content("{}")).andDo(monitor());
    }

    protected boolean useOtpOk(RequestPostProcessor auth, String otp) throws Exception {
        return cs.jsonPayload(useOtp(auth, otp).andExpect(ok()), OtpResponse.class).isStatus();
    }

    protected UserAmount userAmount(long userId, String actual, String expected, String cumulatedCredit) {
        UserAmount obj = new UserAmount();
        obj.setUserId(userId);
        obj.setActual(new BigDecimal(actual));
        obj.setExpected(new BigDecimal(expected));
        obj.setCumulatedCredit(new BigDecimal(cumulatedCredit));
        return obj;
    }

    protected void validateBoard(String name, UUID boardId, Board actual) {
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

    protected void validateBoardEntry(UUID boardId, Long ownerId, LocalDate date, BigDecimal amount, String category,
                                      String description, BoardEntry actual) {
        assertEquals(boardId, actual.getBoardId());
        assertEquals(ownerId, actual.getOwnerId());
        assertEquals(date, actual.getDate());
        assertEquals(0, amount.compareTo(actual.getAmount()));
        assertEquals(category, actual.getCategory());
        assertEquals(description, actual.getDescription());
    }

    protected void validateBoardSplit(UUID boardId, long userId, Integer fromYear, Integer fromMonth, Integer toYear,
                                      Integer toMonth, BigDecimal value1, BoardSplit actual) {
        assertEquals(boardId, actual.getBoardId());
        assertEquals(userId, actual.getUserId());
        assertEquals(fromYear, actual.getFromYear());
        assertEquals(fromMonth, actual.getFromMonth());
        assertEquals(toYear, actual.getToYear());
        assertEquals(toMonth, actual.getToMonth());
        assertEquals(0, value1.compareTo(actual.getValue1()));
    }

    protected void validateBoardSplits(List<BoardSplit> expected, List<BoardSplit> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach(e -> {
            BoardSplit actualEntry = actual.stream().filter(a -> a.getId().equals(e.getId())).findFirst()
                                           .orElseThrow(() -> new NoSuchElementException("Missing entry for " + e));
            validateBoardSplit(e.getBoardId(), e.getUserId(), e.getFromYear(), e.getFromMonth(), e.getToYear(),
                    e.getToMonth(), e.getValue1(), actualEntry);
        });
    }

    protected void validateMonthlyAnalysis(int year, int month, BigDecimal amount, MonthlyAnalysis actual) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonth());
        assertEquals(0, amount.compareTo(actual.getAmount()));
    }

    protected void validateMonthlyUserAnalysis(int year, int month, List<UserAmount> users,
                                               MonthlyUserAnalysis actual) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonth());
        assertEquals(users, actual.getUsers());
    }

    protected void validateUser(Long id, String uid, User actual) {
        assertEquals(uid, actual.getUid());
        if (id != null) {
            assertEquals(id, actual.getId());
        }
        String username = mockAuth.username(uid);
        assertEquals(username, actual.getUsername());
    }

    protected void validateUserOtp(Long userId, OtpStatus status, UserOtp actual) {
        assertNotNull(actual.getId());
        if (userId != null) {
            assertEquals(userId, actual.getUserId());
        }
        if (status != null) {
            assertEquals(status, actual.getStatus());
        }
        assertNotNull(actual.getValidTo());
    }

}
