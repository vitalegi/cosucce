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
        boardId = boardService.createBoard(userId).getBoardId();
    }

    @Nested
    class GetBoardCategories {

        @Test
        void given_boardExists_then_return() {
            boardCategoryService.addBoardCategory(boardId, "label1", "icon1");
            var actual = boardCategoryService.getBoardCategories(boardId);
            assertEquals(1, actual.size());
            var e = actual.get(0);
            assertEquals(boardId, e.getBoardId());
            assertNotNull(e.getCategoryId());
            assertEquals("icon1", e.getIcon());
            assertEquals("label1", e.getLabel());
            assertNotNull(e.getLastUpdate());
            assertNotNull(e.getCreationDate());
        }

        @Test
        void given_boardExists_then_returnAll() {
            boardCategoryService.addBoardCategory(boardId, "label1", null);
            boardCategoryService.addBoardCategory(boardId, "label2", null);
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
            var actual = boardCategoryService.addBoardCategory(boardId, "label1", "icon1");
            assertEquals(boardId, actual.getBoardId());
            assertNotNull(actual.getCategoryId());
            assertEquals("icon1", actual.getIcon());
            assertEquals("label1", actual.getLabel());
            assertTrue(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.addBoardCategory(id, "label1", "icon1"));
            assertEquals("Board " + id + " not found", e.getMessage());
        }
    }

    @Nested
    class UpdateBoardCategories {
        @Test
        void given_boardExists_then_update() {
            var element = boardCategoryService.addBoardCategory(boardId, "label1", "icon1");
            var actual = boardCategoryService.updateBoardCategory(boardId, element.getCategoryId(), "label2", "icon2", false);
            assertEquals(boardId, actual.getBoardId());
            assertEquals(element.getCategoryId(), actual.getCategoryId());
            assertEquals("icon2", actual.getIcon());
            assertEquals("label2", actual.getLabel());
            assertFalse(actual.isEnabled());
            assertNotNull(actual.getLastUpdate());
            assertNotNull(actual.getCreationDate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var element = boardCategoryService.addBoardCategory(boardId, "label1", "icon1");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(id, element.getCategoryId(), "label1", "icon1", true));
            assertEquals("Category " + element.getCategoryId() + " is not part of board " + id, e.getMessage());
        }

        @Test
        void given_accountIsNotPartOfBoard_then_fail() {
            var boardId2 = boardService.createBoard(userId).getBoardId();
            var element = boardCategoryService.addBoardCategory(boardId, "label1", "icon1");
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(boardId2, element.getCategoryId(), "label1", "icon1", true));
            assertEquals("Category " + element.getCategoryId() + " is not part of board " + boardId2, e.getMessage());
        }

        @Test
        void given_accountDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardCategoryService.updateBoardCategory(boardId, id, "label1", "icon1", true));
            assertEquals("Category " + id + " not found", e.getMessage());
        }
    }
}
