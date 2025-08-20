package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.dto.UpdateBoardCategoryDto;
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
public class BoardCategoryServiceTests {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardCategoryService boardCategoryService;
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
    class GetBoardCategorys {

        @Test
        void given_boardExists_then_return() {
            var categoryId = UUID.randomUUID();
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            var actual = boardCategoryService.getBoardCategories(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertEquals(categoryId, e.getCategoryId());
            assertEquals(ICON1, e.getIcon());
            assertEquals(LABEL1, e.getLabel());
            assertEquals(ETAG1, e.getEtag());
            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().build());
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().build());
            var actual = boardCategoryService.getBoardCategories(boardId);
            assertEquals(2, actual.size());
        }

        @Test
        void given_boardDoesNotExist_then_returnEmpty() {
            var id = UUID.randomUUID();
            var actual = boardCategoryService.getBoardCategories(id);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    class AddBoardCategorys {
        @Test
        void given_boardExists_then_addCategory() {
            var categoryId = UUID.randomUUID();
            var element = budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build();
            var actual = boardCategoryService.addBoardCategory(boardId, element);
            assertEquals(boardId, actual.getBoardId());
            assertEquals(categoryId, actual.getCategoryId());
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
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.addBoardCategory(id, budgetUtil.addBoardCategoryDto1().build()));
            assertEquals("Board " + id + " not found", e.getMessage());
        }

        @Test
        void given_duplicateCategoryId_then_fail() {
            var categoryId = UUID.randomUUID();
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            assertThrows(RuntimeException.class, () -> boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto2().categoryId(categoryId).build()));
            var actual = boardCategoryService.getBoardCategory(categoryId);
            assertEquals(LABEL1, actual.getLabel());
            assertEquals(ICON1, actual.getIcon());
            assertEquals(ETAG1, actual.getEtag());
        }
    }

    @Nested
    class UpdateBoardCategorys {
        @Test
        void given_boardExists_then_update() {
            var categoryId = UUID.randomUUID();
            var element = boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            var actual = boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), budgetUtil.updateBoardCategoryDto1().label(LABEL2).icon(ICON2).enabled(false).build());
            assertEquals(boardId, actual.getBoardId());
            assertEquals(element.getCategoryId(), actual.getCategoryId());
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
            var categoryId = UUID.randomUUID();
            var element = boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(id, element.getCategoryId(), budgetUtil.updateBoardCategoryDto1().build()));
            assertEquals("Category " + element.getCategoryId() + " is not part of board " + id, e.getMessage());
        }

        @Test
        void given_categoryIsNotPartOfBoard_then_fail() {
            var boardId2 = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId).getBoardId();
            var categoryId = UUID.randomUUID();
            var element = boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(boardId2, element.getCategoryId(), budgetUtil.updateBoardCategoryDto1().build()));
            assertEquals("Category " + element.getCategoryId() + " is not part of board " + boardId2, e.getMessage());
        }

        @Test
        void given_categoryDoesNotExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(boardId, id, budgetUtil.updateBoardCategoryDto1().build()));
            assertEquals("Category " + id + " not found", e.getMessage());
        }

        @Test
        void given_categoryHasOldEtag_then_fail() {
            var categoryId = UUID.randomUUID();
            var element = boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), budgetUtil.updateBoardCategoryDto1().build());
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), budgetUtil.updateBoardCategoryDto1().build()));
            assertEquals(element.getCategoryId(), e.getId());
            assertEquals(ETAG2, e.getExpectedETag());
            assertEquals(ETAG1, e.getActualETag());
        }
    }

    @Nested
    class DeleteBoardCategory {
        @Test
        void given_entryExists_then_delete() {
            var categoryId = UUID.randomUUID();
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            boardCategoryService.deleteBoardCategory(boardId, categoryId);

            var entries = boardCategoryService.getBoardCategories(boardId);
            assertEquals(0, entries.size(), "Required entry is deleted");

            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build());
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().build());
            boardCategoryService.deleteBoardCategory(boardId, categoryId);
            entries = boardCategoryService.getBoardCategories(boardId);
            assertEquals(1, entries.size(), "Other entries are preserved");
        }

        @Test
        void given_boardDoesNotExist_then_ignore() {
            var fakeId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();
            boardCategoryService.deleteBoardCategory(fakeId, categoryId);
        }

        @Test
        void given_entryDoesNotExist_then_ignore() {
            boardCategoryService.addBoardCategory(boardId, budgetUtil.addBoardCategoryDto1().build());
            boardCategoryService.deleteBoardCategory(boardId, UUID.randomUUID());
        }
    }
}
