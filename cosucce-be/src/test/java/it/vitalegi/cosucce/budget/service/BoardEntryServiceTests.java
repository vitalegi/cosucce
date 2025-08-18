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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {App.class})
@Slf4j
@ActiveProfiles("test")
public class BoardEntryServiceTests {
    private final BigDecimal AMOUNT1 = new BigDecimal("10.1234");
    private final BigDecimal AMOUNT2 = BigDecimal.ONE.divide(new BigDecimal("3"), 4, RoundingMode.CEILING);
    @Autowired
    BoardService boardService;
    @Autowired
    BoardEntryService boardEntryService;
    @Autowired
    BoardAccountService boardAccountService;
    @Autowired
    BoardCategoryService boardCategoryService;

    UUID userId;
    UUID boardId;
    UUID accountId;
    UUID categoryId;

    @BeforeEach
    void init() {
        userId = UserUtil.createUser();
        boardId = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
        accountId = boardAccountService.addBoardAccount(boardId, UUID.randomUUID(), "acc", null, "a").getAccountId();
        categoryId = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "cat", null, "a").getCategoryId();
    }

    @Nested
    class GetBoardEntries {

        @Test
        void given_boardExists_then_return() {
            var entryId = UUID.randomUUID();
            boardEntryService.addBoardEntry(boardId, entryId, accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var actual = boardEntryService.getBoardEntries(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertNotNull(e.getEntryId());
            assertEquals(accountId, e.getAccountId());
            assertEquals(categoryId, e.getCategoryId());
            assertEquals("desc", e.getDescription());
            assertEquals(0, AMOUNT1.compareTo(e.getAmount()));
            assertEquals(userId, e.getLastUpdatedBy());

            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc1", AMOUNT1, "a", userId);
            boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc2", AMOUNT1, "a", userId);
            var actual = boardEntryService.getBoardEntries(boardId);
            assertEquals(2, actual.size());
        }

        @Test
        void given_entryOnDifferentBoard_then_dontReturn() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc1", AMOUNT1, "a", userId);
            var actual = boardEntryService.getBoardEntries(boardId2);
            assertEquals(0, actual.size());
        }

        @Test
        void given_boardDoesntExist_then_returnEmpty() {
            var id = UUID.randomUUID();
            var actual = boardEntryService.getBoardEntries(id);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    class AddBoardEntry {
        @Test
        void given_boardExists_then_addEntry() {
            var actual = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getEntryId());
            assertEquals("a", actual.getEtag());
            assertEquals(accountId, actual.getAccountId());
            assertEquals(categoryId, actual.getCategoryId());
            assertEquals("desc", actual.getDescription());
            assertEquals(0, AMOUNT1.compareTo(actual.getAmount()));
            assertEquals(userId, actual.getLastUpdatedBy());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardExists_then_fail() {
            var userId = UserUtil.createUser();
            var entryId = UUID.randomUUID();
            boardEntryService.addBoardEntry(boardId, entryId, accountId, categoryId, "desc", AMOUNT1, "a", userId);
            assertThrows(RuntimeException.class, () -> boardEntryService.addBoardEntry(boardId, entryId, accountId, categoryId, "desc2", AMOUNT1, "a", userId));
            var actual = boardEntryService.getBoardEntry(entryId);
            assertEquals("desc", actual.getDescription());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(fakeId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId));
            assertEquals("Board " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_accountDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), fakeId, categoryId, "desc", AMOUNT1, "a", userId));
            assertEquals("Account " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_accountConnectedToDifferentBoard_then_fail() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            accountId = boardAccountService.addBoardAccount(boardId2, UUID.randomUUID(), "", "", "a").getAccountId();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId));
            assertEquals("Account " + accountId + " is not part of board " + boardId, e.getMessage());
        }

        @Test
        void given_categoryDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, fakeId, "desc", AMOUNT1, "a", userId));
            assertEquals("Category " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_categoryConnectedToDifferentBoard_then_fail() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            categoryId = boardCategoryService.addBoardCategory(boardId2, UUID.randomUUID(), "", "", "a").getCategoryId();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId));
            assertEquals("Category " + categoryId + " is not part of board " + boardId, e.getMessage());
        }
    }

    @Nested
    class UpdateBoardEntry {
        @Test
        void given_entryExists_then_updateEntry() {
            var accountId2 = boardAccountService.addBoardAccount(boardId, UUID.randomUUID(), "acc2", "", "a").getAccountId();
            var categoryId2 = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "cat2", "", "a").getCategoryId();

            var original = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var actual = boardEntryService.updateBoardEntry(boardId, original.getEntryId(), accountId2, categoryId2, "desc2", AMOUNT2, userId, "a", "b");

            assertEquals(boardId, actual.getBoardId());
            assertEquals(original.getEntryId(), actual.getEntryId());
            assertEquals("b", actual.getEtag());
            assertEquals(accountId2, actual.getAccountId());
            assertEquals(categoryId2, actual.getCategoryId());
            assertEquals("desc2", actual.getDescription());
            assertEquals(0, AMOUNT2.compareTo(actual.getAmount()));
            assertEquals(userId, actual.getLastUpdatedBy());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(fakeId, entry.getEntryId(), accountId, categoryId, "desc", AMOUNT1, userId, "a", "b"));
            assertEquals("Entry " + entry.getEntryId() + " is not part of board " + fakeId, e.getMessage());
        }

        @Test
        void given_accountDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), fakeId, categoryId, "desc", AMOUNT1, userId, "a", "b"));
            assertEquals("Account " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_accountConnectedToDifferentBoard_then_fail() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            accountId = boardAccountService.addBoardAccount(boardId2, UUID.randomUUID(), "", "", "a").getAccountId();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), accountId, categoryId, "desc", AMOUNT1, userId, "a", "b"));
            assertEquals("Account " + accountId + " is not part of board " + boardId, e.getMessage());
        }

        @Test
        void given_categoryDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), accountId, fakeId, "desc", AMOUNT1, userId, "a", "b"));
            assertEquals("Category " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_categoryConnectedToDifferentBoard_then_fail() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            categoryId = boardCategoryService.addBoardCategory(boardId2, UUID.randomUUID(), "", "", "a").getCategoryId();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), accountId, categoryId, "desc", AMOUNT1, userId, "a", "b"));
            assertEquals("Category " + categoryId + " is not part of board " + boardId, e.getMessage());
        }

        @Test
        void given_entryHasOldVersion_then_fail() {
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), accountId, categoryId, "desc", AMOUNT1, userId, "a", "b");
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), accountId, categoryId, "desc", AMOUNT1, userId, "a", "c"));
            assertEquals(entry.getEntryId(), e.getId());
            assertEquals("b", e.getExpectedETag());
            assertEquals("a", e.getActualETag());
        }
    }


    @Nested
    class DeleteBoardEntry {
        @Test
        void given_entryExists_then_delete() {
            var original = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var actual = boardEntryService.deleteBoardEntry(boardId, original.getEntryId());
            assertEquals(boardId, actual.getBoardId());
            assertEquals(original.getEntryId(), actual.getEntryId());
            assertEquals(accountId, actual.getAccountId());
            assertEquals(categoryId, actual.getCategoryId());
            assertEquals("desc", actual.getDescription());
            assertEquals(0, AMOUNT1.compareTo(actual.getAmount()));
            assertEquals(userId, actual.getLastUpdatedBy());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());

            var entries = boardEntryService.getBoardEntries(boardId);
            assertEquals(0, entries.size());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, UUID.randomUUID(), accountId, categoryId, "desc", AMOUNT1, "a", userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.deleteBoardEntry(fakeId, entry.getEntryId()));
            assertEquals("Entry " + entry.getEntryId() + " is not part of board " + fakeId, e.getMessage());
        }
    }
}
