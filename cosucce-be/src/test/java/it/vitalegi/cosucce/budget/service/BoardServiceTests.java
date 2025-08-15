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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Nested
    class AddBoard {

        @Test
        void boardIsCreated() {
            var userId = UserUtil.createUser();
            var boardId = UUID.randomUUID();
            var board = boardService.addBoard(boardId, "name", userId);
            assertEquals(boardId, board.getBoardId());
            assertEquals(0, board.getVersion());
            assertNotNull(board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());
            assertTrue(boardRepository.findById(board.getBoardId()).isPresent());
        }

        @Test
        void boardUserIsCreatedWithOwnerRole() {
            var userId = UserUtil.createUser();

            var board = boardService.addBoard(UUID.randomUUID(),"name", userId);
            var entity = boardUserRepository.findById(new BoardUserId(board.getBoardId(), userId));
            assertTrue(entity.isPresent());
            assertEquals(BoardUserRole.OWNER.name(), entity.get().getRole());
        }
    }

    @Nested
    class GetVisibleBoards {
        @Test
        void given_userHasBoards_then_returnAllVisibleBoards() {
            var userId1 = UserUtil.createUser();
            var userId2 = UserUtil.createUser();

            var board1 = boardService.addBoard(UUID.randomUUID(),"name", userId1);
            var board2 = boardService.addBoard(UUID.randomUUID(),"name", userId1);
            var board3 = boardService.addBoard(UUID.randomUUID(),"name", userId2);

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
            var board = boardService.addBoard(UUID.randomUUID(),"name", userId);
            var actual = boardService.updateBoard(board.getBoardId(), "New name", 0);
            assertEquals(board.getBoardId(), actual.getBoardId());
            assertEquals(1, actual.getVersion());
            assertEquals("New name", actual.getName());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardService.updateBoard(id, "", 0));
            assertEquals("Board " + id + " not found", e.getMessage());
        }

        @Test
        void given_entryHasOldVersion_then_fail() {
            var userId = UserUtil.createUser();
            var board = boardService.addBoard(UUID.randomUUID(),"name", userId);
            var entry = boardService.updateBoard(board.getBoardId(), "New name", 0);
            var e = Assertions.assertThrows(OptimisticLockException.class, () -> boardService.updateBoard(board.getBoardId(), "New name", 0));
            assertEquals(board.getBoardId(), e.getId());
            assertEquals(1, e.getExpectedVersion());
            assertEquals(0, e.getActualVersion());
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void given_boardExists_then_deleteBoard() {
            var userId = UserUtil.createUser();
            var board = boardService.addBoard(UUID.randomUUID(),"name", userId);
            var actual = boardService.deleteBoard(board.getBoardId());
            assertEquals(board.getBoardId(), actual.getBoardId());
            assertNotNull(actual.getName());
            assertNotNull(actual.getCreationDate());
            assertNotNull(actual.getLastUpdate());
            assertFalse(boardRepository.findById(board.getBoardId()).isPresent());
        }

        @Test
        void given_boardDoesntExist_then_fail() {
            var id = UUID.randomUUID();
            var e = Assertions.assertThrows(BudgetException.class, () -> boardService.deleteBoard(id));
            assertEquals("Board " + id + " not found", e.getMessage());
        }
    }
}
