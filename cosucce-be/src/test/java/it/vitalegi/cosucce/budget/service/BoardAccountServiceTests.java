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

import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ICON1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ICON2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.LABEL1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.LABEL2;
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
    @Autowired
    BudgetUtil budgetUtil;

    UUID userId;
    UUID boardId;

    @BeforeEach
    void init() {
        userId = UserUtil.createUser();
        boardId = budgetUtil.addBoard1(userId).getBoardId();
    }

    @Nested
    class GetBoardAccounts {

        @Test
        void given_boardExists_then_return() {
            var accountId = UUID.randomUUID();
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            var actual = boardAccountService.getBoardAccounts(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertEquals(accountId, e.getAccountId());
            assertEquals(ICON1, e.getIcon());
            assertEquals(LABEL1, e.getLabel());
            assertEquals(ETAG1, e.getEtag());
            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().build());
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().build());
            var actual = boardAccountService.getBoardAccounts(boardId);
            assertEquals(2, actual.size());
        }

        @Test
        void given_boardDoesNotExist_then_returnEmpty() {
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
            var element = budgetUtil.addBoardAccountDto1().accountId(accountId).build();
            var actual = boardAccountService.addBoardAccount(boardId, element);
            assertEquals(boardId, actual.getBoardId());
            assertEquals(accountId, actual.getAccountId());
            assertEquals(ICON1, actual.getIcon());
            assertEquals(LABEL1, actual.getLabel());
            assertEquals(ETAG1, actual.getEtag());
            assertTrue(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesNotExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.addBoardAccount(id, budgetUtil.addBoardAccountDto1().build()));
            assertEquals("Board " + id + " not found", e.getMessage());
        }

        @Test
        void given_duplicateAccountId_then_fail() {
            var accountId = UUID.randomUUID();
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            assertThrows(RuntimeException.class, () -> boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto2().accountId(accountId).build()));
            var actual = boardAccountService.getBoardAccount(accountId);
            assertEquals(LABEL1, actual.getLabel());
            assertEquals(ICON1, actual.getIcon());
            assertEquals(ETAG1, actual.getEtag());
        }
    }

    @Nested
    class UpdateBoardAccounts {
        @Test
        void given_boardExists_then_update() {
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            var actual = boardAccountService.updateBoardAccount(boardId, element.getAccountId(), budgetUtil.updateBoardAccountDto1().label(LABEL2).icon(ICON2).enabled(false).build());
            assertEquals(boardId, actual.getBoardId());
            assertEquals(element.getAccountId(), actual.getAccountId());
            assertEquals(ICON2, actual.getIcon());
            assertEquals(LABEL2, actual.getLabel());
            assertEquals(ETAG2, actual.getEtag(), "ETag is changed");
            assertFalse(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesNotExist_then_fail() {
            var id = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(id, element.getAccountId(), budgetUtil.updateBoardAccountDto1().build()));
            assertEquals("Account " + element.getAccountId() + " is not part of board " + id, e.getMessage());
        }

        @Test
        void given_accountIsNotPartOfBoard_then_fail() {
            var boardId2 = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId).getBoardId();
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(boardId2, element.getAccountId(), budgetUtil.updateBoardAccountDto1().build()));
            assertEquals("Account " + element.getAccountId() + " is not part of board " + boardId2, e.getMessage());
        }

        @Test
        void given_accountDoesNotExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardAccountService.updateBoardAccount(boardId, id, budgetUtil.updateBoardAccountDto1().build()));
            assertEquals("Account " + id + " not found", e.getMessage());
        }

        @Test
        void given_accountHasOldEtag_then_fail() {
            var accountId = UUID.randomUUID();
            var element = boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            boardAccountService.updateBoardAccount(boardId, element.getAccountId(), budgetUtil.updateBoardAccountDto1().build());
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardAccountService.updateBoardAccount(boardId, element.getAccountId(), budgetUtil.updateBoardAccountDto1().build()));
            assertEquals(element.getAccountId(), e.getId());
            assertEquals(ETAG2, e.getExpectedETag());
            assertEquals(ETAG1, e.getActualETag());
        }
    }

    @Nested
    class DeleteBoardAccount {
        @Test
        void given_entryExists_then_delete() {
            var accountId = UUID.randomUUID();
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            boardAccountService.deleteBoardAccount(boardId, accountId);

            var entries = boardAccountService.getBoardAccounts(boardId);
            assertEquals(0, entries.size(), "Required entry is deleted");

            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().accountId(accountId).build());
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().build());
            boardAccountService.deleteBoardAccount(boardId, accountId);
            entries = boardAccountService.getBoardAccounts(boardId);
            assertEquals(1, entries.size(), "Other entries are preserved");
        }

        @Test
        void given_boardDoesNotExist_then_ignore() {
            var fakeId = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            boardAccountService.deleteBoardAccount(fakeId, accountId);
        }

        @Test
        void given_entryDoesNotExist_then_ignore() {
            boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().build());
            boardAccountService.deleteBoardAccount(boardId, UUID.randomUUID());
        }
    }
}
