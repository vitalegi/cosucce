package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        boardId = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
    }

    @Nested
    class GetBoardAccounts {

        @Test
        void given_boardExists_then_return() {
            var accountId = UUID.randomUUID();
            boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "a");
            var actual = boardAccountService.getBoardAccounts(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertEquals(accountId, e.getAccountId());
            assertEquals("icon1", e.getIcon());
            assertEquals("label1", e.getLabel());
            assertEquals("a", e.getEtag());
            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardAccountService.addBoardAccount(boardId, UUID.randomUUID(), "label1", null, "a");
            boardAccountService.addBoardAccount(boardId, UUID.randomUUID(), "label2", null, "a");
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
            var accountId = UUID.randomUUID();
            var actual = boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "a");
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getAccountId());
            assertEquals("icon1", actual.getIcon());
            assertEquals("label1", actual.getLabel());
            assertEquals("a", actual.getEtag());
            assertTrue(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.addBoardAccount(id, accountId, "label1", "icon1", "a"));
            assertEquals("Board " + id + " not found", e.getMessage());
        }

        @Test
        void given_boardExists_then_fail() {
            var accountId = UUID.randomUUID();
            boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "a");
            assertThrows(RuntimeException.class, () -> boardAccountService.addBoardAccount(boardId, accountId, "label2", "icon1", "a"));
            var actual = boardAccountService.getBoardAccount(accountId);
            assertEquals("label1", actual.getLabel());
        }
    }

    @Nested
    class UpdateBoardAccounts {
        @Test
        void given_boardExists_then_update() {
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "1");
            var actual = boardAccountService.updateBoardAccount(boardId, element.getAccountId(), "label2", "icon2", false, "1", "2");
            assertEquals(boardId, actual.getBoardId());
            assertEquals(element.getAccountId(), actual.getAccountId());
            assertEquals("icon2", actual.getIcon());
            assertEquals("label2", actual.getLabel());
            assertEquals("2", actual.getEtag(), "ETag is changed");
            assertFalse(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "a");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(id, element.getAccountId(), "label1", "icon1", true, "a", "b"));
            assertEquals("Account " + element.getAccountId() + " is not part of board " + id, e.getMessage());
        }

        @Test
        void given_accountIsNotPartOfBoard_then_fail() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "a");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(boardId2, element.getAccountId(), "label1", "icon1", true, "a", "b"));
            assertEquals("Account " + element.getAccountId() + " is not part of board " + boardId2, e.getMessage());
        }

        @Test
        void given_accountDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(boardId, id, "label1", "icon1", true, "a", "b"));
            assertEquals("Account " + id + " not found", e.getMessage());
        }

        @Test
        void given_accountHasOldEtag_then_fail() {
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, accountId, "label1", "icon1", "a");
            boardAccountService.updateBoardAccount(boardId, element.getAccountId(), "label2", "icon2", false, "a", "b");
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardAccountService.updateBoardAccount(boardId, element.getAccountId(), "label2", "icon2", false, "a", "c"));
            assertEquals(element.getAccountId(), e.getId());
            assertEquals("b", e.getExpectedETag());
            assertEquals("a", e.getActualETag());
        }
    }

    @Nested
    class DeleteBoardAccount {
        @Test
        void given_entryExists_then_delete() {
            var accountId = UUID.randomUUID();
            var original = boardAccountService.addBoardAccount(boardId, accountId, "label", "icon", "a");
            var actual = boardAccountService.deleteBoardAccount(boardId, original.getAccountId());
            assertEquals(boardId, actual.getBoardId());
            assertEquals(original.getAccountId(), actual.getAccountId());
            assertEquals("label", actual.getLabel());
            assertEquals("icon", actual.getIcon());
            assertTrue(actual.isEnabled());
            assertEquals("a", actual.getEtag());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());

            var entries = boardAccountService.getBoardAccounts(boardId);
            assertEquals(0, entries.size());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var entry = boardAccountService.addBoardAccount(boardId, accountId, "label", "icon", "a");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.deleteBoardAccount(fakeId, entry.getAccountId()));
            assertEquals("Account " + entry.getAccountId() + " is not part of board " + fakeId, e.getMessage());
        }
    }
}
