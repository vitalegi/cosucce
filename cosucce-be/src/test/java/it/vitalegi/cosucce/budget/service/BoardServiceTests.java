package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.constants.BoardUserRole;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.repository.BoardRepository;
import it.vitalegi.cosucce.budget.repository.BoardUserRepository;
import it.vitalegi.cosucce.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.NAME1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.NAME2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {App.class})
@Slf4j
@ActiveProfiles("test")
public class BoardServiceTests {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardUserRepository boardUserRepository;
    @Autowired
    BudgetUtil budgetUtil;

    @Nested
    class AddBoard {

        @Test
        void boardIsCreated() {
            var userId = UserUtil.createUser();
            var boardId = UUID.randomUUID();
            var board = boardService.addBoard(budgetUtil.addBoardDto1().boardId(boardId).build(), userId);
            assertEquals(boardId, board.getBoardId());
            assertEquals(ETAG1, board.getEtag());
            assertEquals(NAME1, board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());
            assertTrue(boardRepository.findById(board.getBoardId()).isPresent());
        }

        @Test
        void given_boardIsCreated_then_boardUserIsCreatedWithOwnerRole() {
            var userId = UserUtil.createUser();

            var boardId = UUID.randomUUID();
            var board = boardService.addBoard(budgetUtil.addBoardDto1().boardId(boardId).build(), userId);
            var entity = boardUserRepository.findById(new BoardUserId(board.getBoardId(), userId));
            assertTrue(entity.isPresent());
            assertEquals(BoardUserRole.OWNER.name(), entity.get().getRole());
        }

        @Test
        void given_boardExists_then_fail() {
            var userId = UserUtil.createUser();
            var boardId = UUID.randomUUID();
            boardService.addBoard(budgetUtil.addBoardDto1().boardId(boardId).build(), userId);
            assertThrows(RuntimeException.class, () -> boardService.addBoard(budgetUtil.addBoardDto2().boardId(boardId).build(), userId));
            var actual = boardService.getBoard(boardId);
            assertEquals(NAME1, actual.getName());
            assertEquals(ETAG1, actual.getEtag());
        }
    }

    @Nested
    class GetVisibleBoards {
        @Test
        void given_userHasBoards_then_returnAllVisibleBoards() {
            var userId1 = UserUtil.createUser();
            var userId2 = UserUtil.createUser();

            var board1 = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId1);
            var board2 = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId1);
            var board3 = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId2);

            var actuals = boardService.getVisibleBoards(userId1);
            assertEquals(2, actuals.size());

            actuals = boardService.getVisibleBoards(userId2);
            assertEquals(1, actuals.size());
        }

        @Test
        void given_userHasNoVisibleBoard_then_returnEmptyArray() {
            var userId1 = UserUtil.createUser();
            var actual = boardService.getVisibleBoards(userId1);
            assertEquals(0, actual.size());
        }
    }

    @Nested
    class UpdateBoard {
        @Test
        void given_boardExists_then_updateBoard() {
            var userId = UserUtil.createUser();
            var board = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId);
            var actual = boardService.updateBoard(board.getBoardId(), budgetUtil.updateBoardDto1().build());
            assertEquals(board.getBoardId(), actual.getBoardId());
            assertEquals(ETAG2, actual.getEtag());
            assertEquals(NAME2, actual.getName());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
        }

        @Test
        void given_boardDoesNotExist_then_fail() {
            var fakeId = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardService.updateBoard(fakeId, budgetUtil.updateBoardDto1().build()));
            assertEquals("Board " + fakeId + " not found", e.getMessage());
        }

        @Test
        void given_entryHasOldVersion_then_fail() {
            var userId = UserUtil.createUser();
            var board = boardService.addBoard(budgetUtil.addBoardDto1().build(), userId);
            var actual = boardService.updateBoard(board.getBoardId(), budgetUtil.updateBoardDto1().build());
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardService.updateBoard(board.getBoardId(), budgetUtil.updateBoardDto1().build()));
            assertEquals(board.getBoardId(), e.getId());
            assertEquals(ETAG2, e.getExpectedETag());
            assertEquals(ETAG1, e.getActualETag());
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void given_entryExists_then_delete() {
            var userId = UserUtil.createUser();
            var boardId = UUID.randomUUID();
            boardService.addBoard(budgetUtil.addBoardDto1().boardId(boardId).build(), userId);
            boardService.deleteBoard(boardId);

            var entries = boardService.getVisibleBoards(userId);
            assertEquals(0, entries.size(), "Required entry is deleted");

            boardService.addBoard(budgetUtil.addBoardDto1().boardId(boardId).build(), userId);
            boardService.addBoard(budgetUtil.addBoardDto1().build(), userId);
            boardService.deleteBoard(boardId);

            entries = boardService.getVisibleBoards(userId);
            assertEquals(1, entries.size(), "Other entries are preserved");
        }

        @Test
        void given_boardDoesNotExist_then_ignore() {
            var fakeId = UUID.randomUUID();
            boardService.deleteBoard(fakeId);
        }
    }
}
