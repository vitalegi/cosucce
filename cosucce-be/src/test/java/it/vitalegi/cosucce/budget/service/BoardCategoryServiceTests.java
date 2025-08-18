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
public class BoardCategoryServiceTests {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardCategoryService boardCategoryService;

    UUID userId;
    UUID boardId;

    @BeforeEach
    void init() {
        userId = UserUtil.createUser();
        boardId = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
    }

    @Nested
    class GetBoardCategories {

        @Test
        void given_boardExists_then_return() {
            var categoryId = UUID.randomUUID();
            boardCategoryService.addBoardCategory(boardId, categoryId, "label1", "icon1", "a");
            var actual = boardCategoryService.getBoardCategories(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertEquals(categoryId, e.getCategoryId());
            assertEquals("icon1", e.getIcon());
            assertEquals("label1", e.getLabel());
            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label1", null, "a");
            boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label2", null, "a");
            var actual = boardCategoryService.getBoardCategories(boardId);
            assertEquals(2, actual.size());
        }

        @Test
        void given_boardDoesntExist_then_returnEmpty() {
            var id = UUID.randomUUID();
            var actual = boardCategoryService.getBoardCategories(id);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    class AddBoardCategories {
        @Test
        void given_boardExists_then_addAccount() {
            var actual = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label1", "icon1", "a");
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getCategoryId());
            assertEquals("a", actual.getEtag());
            assertEquals("icon1", actual.getIcon());
            assertEquals("label1", actual.getLabel());
            assertTrue(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.addBoardCategory(id, UUID.randomUUID(), "label1", "icon1", "a"));
            assertEquals("Board " + id + " not found", e.getMessage());
        }

        @Test
        void given_boardExists_then_fail() {
            var categoryId = UUID.randomUUID();
            boardCategoryService.addBoardCategory(boardId, categoryId, "label1", "icon1", "a");
            assertThrows(RuntimeException.class, () -> boardCategoryService.addBoardCategory(boardId, categoryId, "label2", "icon1", "a"));
            var actual = boardCategoryService.getBoardCategory(categoryId);
            assertEquals("label1", actual.getLabel());
        }
    }

    @Nested
    class UpdateBoardCategories {
        @Test
        void given_boardExists_then_update() {
            var element = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label1", "icon1", "a");
            var actual = boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), "label2", "icon2", false, "a", "b");
            assertEquals(boardId, actual.getBoardId());
            assertEquals(element.getCategoryId(), actual.getCategoryId());
            assertEquals("b", actual.getEtag());
            assertEquals("icon2", actual.getIcon());
            assertEquals("label2", actual.getLabel());
            assertFalse(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var element = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label1", "icon1", "a");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(id, element.getCategoryId(), "label1", "icon1", true, "a", "b"));
            assertEquals("Category " + element.getCategoryId() + " is not part of board " + id, e.getMessage());
        }

        @Test
        void given_categoryIsNotPartOfBoard_then_fail() {
            var boardId2 = boardService.addBoard(UUID.randomUUID(), "name", "a", userId).getBoardId();
            var element = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label1", "icon1", "a");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(boardId2, element.getCategoryId(), "label1", "icon1", true, "a", "b"));
            assertEquals("Category " + element.getCategoryId() + " is not part of board " + boardId2, e.getMessage());
        }

        @Test
        void given_categoryDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(boardId, id, "label1", "icon1", true, "a", "b"));
            assertEquals("Category " + id + " not found", e.getMessage());
        }

        @Test
        void given_categoryHasOldVersion_then_fail() {
            var element = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label1", "icon1", "a");
            boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), "label2", "icon2", false, "a", "b");
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), "label2", "icon2", false, "a", "c"));
            assertEquals(element.getCategoryId(), e.getId());
            assertEquals("b", e.getExpectedETag());
            assertEquals("a", e.getActualETag());
        }
    }

    @Nested
    class DeleteBoardCategory {
        @Test
        void given_entryExists_then_delete() {
            var original = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label", "icon", "a");
            var actual = boardCategoryService.deleteBoardCategory(boardId, original.getCategoryId());
            assertEquals(boardId, actual.getBoardId());
            assertEquals(original.getCategoryId(), actual.getCategoryId());
            assertEquals("label", actual.getLabel());
            assertEquals("icon", actual.getIcon());
            assertTrue(actual.isEnabled());
            assertEquals("a", actual.getEtag());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());

            var entries = boardCategoryService.getBoardCategories(boardId);
            assertEquals(0, entries.size());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var entry = boardCategoryService.addBoardCategory(boardId, UUID.randomUUID(), "label", "icon", "a");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.deleteBoardCategory(fakeId, entry.getCategoryId()));
            assertEquals("Category " + entry.getCategoryId() + " is not part of board " + fakeId, e.getMessage());
        }
    }
}
