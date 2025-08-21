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

import static it.vitalegi.cosucce.budget.service.BudgetUtil.AMOUNT1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.AMOUNT2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DATE1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DATE2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DESCRIPTION1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DESCRIPTION2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {App.class})
@Slf4j
@ActiveProfiles("test")
public class BoardEntryServiceTests {

    @Autowired
    BoardService boardService;
    @Autowired
    BoardEntryService boardEntryService;
    @Autowired
    BoardAccountService boardAccountService;
    @Autowired
    BoardCategoryService boardCategoryService;
    @Autowired
    BudgetUtil budgetUtil;

    UUID userId;
    UUID boardId;
    UUID accountId;
    UUID categoryId;

    @BeforeEach
    void init() {
        userId = UserUtil.createUser();
        boardId = budgetUtil.addBoard1(userId).getBoardId();
        accountId = boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto1().build()).getAccountId();
        categoryId = boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().build()).getCategoryId();
    }

    @Nested
    class GetBoardEntries {
        @Test
        void given_boardExists_then_return() {
            var entryId = UUID.randomUUID();
            boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).entryId(entryId).build(), userId);
            var actual = boardEntryService.getBoardEntries(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertNotNull(e.getEntryId());
            assertEquals(DATE1, e.getDate());
            assertEquals(accountId, e.getAccountId());
            assertEquals(categoryId, e.getCategoryId());
            assertEquals(DESCRIPTION1, e.getDescription());
            assertEquals(0, AMOUNT1.compareTo(e.getAmount()));
            assertEquals(userId, e.getLastUpdatedBy());

            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto2(accountId, categoryId).build(), userId);
            var actual = boardEntryService.getBoardEntries(boardId);
            assertEquals(2, actual.size());
        }

        @Test
        void given_entryOnDifferentBoard_then_dontReturn() {
            var board2 = budgetUtil.addBoard2(userId);
            var account2 = boardAccountService.addBoardAccount(board2.getBoardId(), budgetUtil.addBoardAccountDto1().build());
            var category2 = boardCategoryService.addBoardCategory(board2.getBoardId(), budgetUtil.addBoardCategoryDto1().build());
            boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            boardEntryService.addBoardEntry(board2.getBoardId(), budgetUtil.addBoardEntryDto2(account2.getAccountId(), category2.getCategoryId()).build(), userId);

            var actual = boardEntryService.getBoardEntries(boardId);

            assertEquals(2, actual.size());
        }

        @Test
        void given_boardDoesNotExist_then_returnEmpty() {
            var id = UUID.randomUUID();
            var actual = boardEntryService.getBoardEntries(id);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    class AddBoardEntry {
        @Test
        void given_boardExists_then_addEntry() {
            var actual = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getEntryId());
            assertEquals(DATE1, actual.getDate());
            assertEquals(ETAG1, actual.getEtag());
            assertEquals(accountId, actual.getAccountId());
            assertEquals(categoryId, actual.getCategoryId());
            assertEquals(DESCRIPTION1, actual.getDescription());
            assertEquals(0, AMOUNT1.compareTo(actual.getAmount()));
            assertEquals(userId, actual.getLastUpdatedBy());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_entryExists_then_fail() {
            var userId = UserUtil.createUser();
            var entryId = UUID.randomUUID();
            var expected = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).entryId(entryId).build(), userId);
            assertEquals(ETAG1, expected.getEtag());
            assertThrows(RuntimeException.class, () -> boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto2(accountId, categoryId).entryId(entryId).build(), userId));
            var actual = boardEntryService.getBoardEntry(entryId);
            assertEquals(ETAG1, actual.getEtag());
        }

        @Test
        void given_boardDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(fakeId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId));
            assertEquals("Board " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_accountDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(fakeId, categoryId).build(), userId));
            assertEquals("Account " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_categoryDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, fakeId).build(), userId));
            assertEquals("Category " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_accountConnectedToDifferentBoard_then_fail() {
            var board2 = budgetUtil.addBoard2(userId);
            var accountId2 = boardAccountService.addBoardAccount(board2.getBoardId(), budgetUtil.addBoardAccountDto2().build()).getAccountId();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId2, categoryId).build(), userId));
            assertEquals("Account " + accountId2 + " is not part of board " + boardId, e.getMessage());
        }


        @Test
        void given_categoryConnectedToDifferentBoard_then_fail() {
            var board2 = budgetUtil.addBoard2(userId);
            var categoryId2 = boardCategoryService.addBoardCategory(board2.getBoardId(), budgetUtil.addBoardCategoryDto2().build()).getCategoryId();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId2).build(), userId));
            assertEquals("Category " + categoryId2 + " is not part of board " + boardId, e.getMessage());
        }
    }

    @Nested
    class UpdateBoardEntry {

        @Test
        void given_entryExists_then_updateEntry() {
            var accountId2 = boardAccountService.addBoardAccount(boardId, budgetUtil.addBoardAccountDto2().build()).getAccountId();
            var categoryId2 = boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto2().build()).getCategoryId();

            var original = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var actual = boardEntryService.updateBoardEntry(boardId, original.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId2).categoryId(categoryId2).build(), userId);

            assertEquals(boardId, actual.getBoardId());
            assertEquals(original.getEntryId(), actual.getEntryId());
            assertEquals(DATE1, actual.getDate());
            assertEquals(ETAG2, actual.getEtag());
            assertEquals(accountId2, actual.getAccountId());
            assertEquals(categoryId2, actual.getCategoryId());
            assertEquals(DESCRIPTION1, actual.getDescription());
            assertEquals(0, AMOUNT1.compareTo(actual.getAmount()));
            assertEquals(userId, actual.getLastUpdatedBy());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_multipleUpdates_then_ok() {
            var original = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var update1 = boardEntryService.updateBoardEntry(boardId, original.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(categoryId).etag(ETAG1).newETag(ETAG2).description("2").build(), userId);
            assertEquals("2", update1.getDescription());
            var update2 = boardEntryService.updateBoardEntry(boardId, original.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(categoryId).etag(ETAG2).newETag(ETAG3).description("3").build(), userId);
            assertEquals("3", update2.getDescription());
        }

        @Test
        void given_boardDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(fakeId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(categoryId).build(), userId));
            assertEquals("Entry " + entry.getEntryId() + " is not part of board " + fakeId, e.getMessage());
        }

        @Test
        void given_accountDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(fakeId).categoryId(categoryId).build(), userId));
            assertEquals("Account " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_categoryDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(fakeId).build(), userId));
            assertEquals("Category " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_accountConnectedToDifferentBoard_then_fail() {
            var board2 = budgetUtil.addBoard2(userId);
            var accountId2 = boardAccountService.addBoardAccount(board2.getBoardId(), budgetUtil.addBoardAccountDto2().build()).getAccountId();
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);

            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId2).categoryId(categoryId).build(), userId));
            assertEquals("Account " + accountId2 + " is not part of board " + boardId, e.getMessage());
        }

        @Test
        void given_categoryConnectedToDifferentBoard_then_fail() {
            var board2 = budgetUtil.addBoard2(userId);
            var categoryId2 = boardCategoryService.addBoardCategory(board2.getBoardId(), budgetUtil.addBoardCategoryDto2().build()).getCategoryId();
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);

            var e = Assertions.assertThrows(BudgetException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(categoryId2).build(), userId));
            assertEquals("Category " + categoryId2 + " is not part of board " + boardId, e.getMessage());
        }

        @Test
        void given_entryHasOldVersion_then_fail() {
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(categoryId).build(), userId);
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardEntryService.updateBoardEntry(boardId, entry.getEntryId(), budgetUtil.updateBoardEntryDto1().accountId(accountId).categoryId(categoryId).build(), userId));
            assertEquals(entry.getEntryId(), e.getId());
            assertEquals(ETAG2, e.getExpectedETag());
            assertEquals(ETAG1, e.getActualETag());
        }
    }

    @Nested
    class DeleteBoardEntry {
        @Test
        void given_entryExists_then_delete() {
            var id = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId).getEntryId();

            boardEntryService.deleteBoardEntry(boardId, id);

            var entries = boardEntryService.getBoardEntries(boardId);
            assertEquals(0, entries.size(), "Required entry is deleted");


            var e1 = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var e2 = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);

            boardEntryService.deleteBoardEntry(boardId, e1.getEntryId());

            entries = boardEntryService.getBoardEntries(boardId);
            assertEquals(1, entries.size(), "Other entries are preserved");
            assertEquals(e2.getEntryId(), entries.get(0).getEntryId());
        }

        @Test
        void given_wrongBoard_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);
            var e = assertThrows(BudgetException.class, () -> boardEntryService.deleteBoardEntry(fakeId, entry.getEntryId()));
            assertEquals("Entry " + entry.getEntryId() + " is not part of board " + fakeId, e.getMessage());
            var entries = boardEntryService.getBoardEntries(boardId);
            assertEquals(1, entries.size());
        }

        @Test
        void given_entryDoesNotExist_then_ignore() {
            var entry = boardEntryService.addBoardEntry(boardId, budgetUtil.addBoardEntryDto1(accountId, categoryId).build(), userId);

            boardEntryService.deleteBoardEntry(boardId, UUID.randomUUID());

            var entries = boardEntryService.getBoardEntries(boardId);
            assertEquals(1, entries.size());
        }
    }
}
