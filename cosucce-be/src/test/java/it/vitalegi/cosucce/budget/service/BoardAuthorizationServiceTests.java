package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.App;
import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.constants.BoardUserRole;
import it.vitalegi.cosucce.budget.entity.BoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import it.vitalegi.cosucce.budget.exception.UnauthorizedBoardAccessException;
import it.vitalegi.cosucce.budget.repository.BoardUserRepository;
import it.vitalegi.cosucce.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {App.class})
@Slf4j
@ActiveProfiles("test")
public class BoardAuthorizationServiceTests {
    @Autowired
    BoardService boardService;
    @Autowired
    BudgetAuthorizationService budgetAuthorizationService;
    @Autowired
    BoardUserRepository boardUserRepository;

    @Nested
    class CheckPermission {

        @Test
        void given_userIsOwnerOfBoard() {
            var userId = UserUtil.createUser();
            var board = boardService.addBoard(UUID.randomUUID(),"name", "a", userId);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId, BoardUserPermission.ADMIN);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId, BoardUserPermission.READ);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId, BoardUserPermission.WRITE);
        }

        @Test
        void given_userIsMemberOfBoard() {
            var userId1 = UserUtil.createUser();
            var userId2 = UserUtil.createUser();
            var board = boardService.addBoard(UUID.randomUUID(), "name", "a",userId1);
            boardUser(board.getBoardId(), userId2, BoardUserRole.MEMBER);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId1, BoardUserPermission.ADMIN);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId1, BoardUserPermission.READ);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId1, BoardUserPermission.WRITE);

            var e = Assertions.assertThrows(UnauthorizedBoardAccessException.class, () -> budgetAuthorizationService.checkPermission(board.getBoardId(), userId2, BoardUserPermission.ADMIN));
            assertEquals(board.getBoardId(), e.getBoardId());
            assertEquals(2, e.getUserPermissions().size());
            assertEquals(BoardUserPermission.ADMIN, e.getMissingPermission());
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId2, BoardUserPermission.READ);
            budgetAuthorizationService.checkPermission(board.getBoardId(), userId2, BoardUserPermission.WRITE);
        }

        @Test
        void given_userIsMemberOfDifferentBoard() {
            var userId1 = UserUtil.createUser();
            var userId2 = UserUtil.createUser();
            var board1 = boardService.addBoard(UUID.randomUUID(),"name", "a", userId1);
            var board2 = boardService.addBoard(UUID.randomUUID(),"name", "a", userId2);

            budgetAuthorizationService.checkPermission(board1.getBoardId(), userId1, BoardUserPermission.ADMIN);
            Assertions.assertThrows(UnauthorizedBoardAccessException.class, () -> budgetAuthorizationService.checkPermission(board1.getBoardId(), userId2, BoardUserPermission.ADMIN));

            budgetAuthorizationService.checkPermission(board2.getBoardId(), userId2, BoardUserPermission.ADMIN);
            Assertions.assertThrows(UnauthorizedBoardAccessException.class, () -> budgetAuthorizationService.checkPermission(board2.getBoardId(), userId1, BoardUserPermission.ADMIN));
        }
    }

    private BoardUserEntity boardUser(UUID boardId, UUID userId, BoardUserRole role) {
        var entity = new BoardUserEntity();
        entity.setId(new BoardUserId(boardId, userId));
        entity.setRole(role.name());
        entity.setCreationDate(Instant.now());
        entity.setLastUpdate(Instant.now());
        return boardUserRepository.save(entity);
    }
}
