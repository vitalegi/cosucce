package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.constants.BoardUserRole;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import it.vitalegi.cosucce.budget.repository.BoardRepository;
import it.vitalegi.cosucce.budget.repository.BoardUserRepository;
import it.vitalegi.cosucce.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    class CreateBoard {

        @Test
        void boardIsCreated() {
            var userId = UserUtil.createUser();

            var board = boardService.createBoard(userId);
            assertNotNull(board.getBoardId());
            assertNotNull(board.getName());
            assertNotNull(board.getCreationDate());
            assertNotNull(board.getLastUpdate());
            assertTrue(boardRepository.findById(board.getBoardId()).isPresent());
        }

        @Test
        void boardUserIsCreatedWithOwnerRole() {
            var userId = UserUtil.createUser();

            var board = boardService.createBoard(userId);
            var entity = boardUserRepository.findById(new BoardUserId(board.getBoardId(), userId));
            assertTrue(entity.isPresent());
            assertEquals(BoardUserRole.OWNER.name(), entity.get().getRole());
        }
    }

    @Nested
    class GetVisibleBoards {
        @Disabled
        @Test
        void given_userHasBoards_then_returnAllVisibleBoards() {
        }

        @Disabled
        @Test
        void given_userHasNoVisibleBoard_then_returnEmptyArray() {
        }
    }

    @Nested
    class UpdateBoard {
        @Disabled
        @Test
        void given_boardExists_then_updateBoard() {
        }

        @Disabled
        @Test
        void given_boardDoesntExist_then_fail() {
        }
    }

    @Nested
    class DeleteBoard {
        @Disabled
        @Test
        void given_boardExists_then_deleteBoard() {
        }

        @Disabled
        @Test
        void given_boardDoesntExist_then_fail() {
        }
    }
}
