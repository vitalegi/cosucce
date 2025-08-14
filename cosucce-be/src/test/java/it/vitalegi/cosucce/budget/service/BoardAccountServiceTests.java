package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {App.class})
@Slf4j
@ActiveProfiles("test")
public class BoardAccountServiceTests {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardAccountService boardAccountService;

    UUID userId;
    UUID boardId;

    @BeforeEach
    void init() {
        userId = UserUtil.createUser();
        boardId = boardService.addBoard(userId).getBoardId();
    }

    @Nested
    class GetBoardAccounts {

        @Test
        void given_boardExists_then_return() {
            boardAccountService.addBoardAccount(boardId, "label1", "icon1");
            var actual = boardAccountService.getBoardAccounts(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertNotNull(e.getAccountId());
            assertEquals("icon1", e.getIcon());
            assertEquals("label1", e.getLabel());
            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardAccountService.addBoardAccount(boardId, "label1", null);
            boardAccountService.addBoardAccount(boardId, "label2", null);
            var actual = boardAccountService.getBoardAccounts(boardId);
            assertEquals(2, actual.size());
        }

        @Test
        void given_boardDoesntExist_then_returnEmpty() {
            var id = UUID.randomUUID();
            var actual = boardAccountService.getBoardAccounts(id);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    class AddBoardAccounts {
        @Test
        void given_boardExists_then_addAccount() {
            var actual = boardAccountService.addBoardAccount(boardId, "label1", "icon1");
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getAccountId());
            assertEquals("icon1", actual.getIcon());
            assertEquals("label1", actual.getLabel());
            assertTrue(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.addBoardAccount(id, "label1", "icon1"));
            assertEquals("Board " + id + " not found", e.getMessage());
        }
    }

    @Nested
    class UpdateBoardAccounts {
        @Test
        void given_boardExists_then_update() {
            var element = boardAccountService.addBoardAccount(boardId, "label1", "icon1");
            var actual = boardAccountService.updateBoardAccount(boardId, element.getAccountId(), "label2", "icon2", false);
            assertEquals(boardId, actual.getBoardId());
            assertEquals(element.getAccountId(), actual.getAccountId());
            assertEquals("icon2", actual.getIcon());
            assertEquals("label2", actual.getLabel());
            assertFalse(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, "label1", "icon1");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(id, element.getAccountId(), "label1", "icon1", true));
            assertEquals("Account " + element.getAccountId() + " is not part of board " + id, e.getMessage());
        }

        @Test
        void given_accountIsNotPartOfBoard_then_fail() {
            var boardId2 = boardService.addBoard(userId).getBoardId();
            var element = boardAccountService.addBoardAccount(boardId, "label1", "icon1");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(boardId2, element.getAccountId(), "label1", "icon1", true));
            assertEquals("Account " + element.getAccountId() + " is not part of board " + boardId2, e.getMessage());
        }

        @Test
        void given_accountDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(boardId, id, "label1", "icon1", true));
            assertEquals("Account " + id + " not found", e.getMessage());
        }
    }
}
