package it.vitalegi.cosucce.board.service;

import it.vitalegi.cosucce.board.constant.BoardUserRole;
import it.vitalegi.cosucce.board.entity.BoardEntity;
import it.vitalegi.cosucce.board.entity.BoardUserEntity;
import it.vitalegi.cosucce.board.repository.BoardRepository;
import it.vitalegi.cosucce.board.repository.BoardUserRepository;
import it.vitalegi.cosucce.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static it.vitalegi.cosucce.board.constant.BoardUserRole.MEMBER;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoardPermissionServiceTests {
    BoardPermissionService service;

    UserService userService;
    BoardRepository boardRepository;
    BoardUserRepository boardUserRepository;

    Optional<BoardEntity> board(BoardEntity boardEntity) {
        return Collections.singletonList(boardEntity)
                          .stream()
                          .findFirst();
    }

    @BeforeEach
    void init() {
        service = new BoardPermissionService();

        userService = mock(UserService.class);
        service.userService = userService;

        boardRepository = mock(BoardRepository.class);
        service.boardRepository = boardRepository;

        boardUserRepository = mock(BoardUserRepository.class);
        service.boardUserRepository = boardUserRepository;
    }

    @DisplayName("hasGrants should have limited permissions if MEMBER")
    @Test
    void test_hasGrant_member_shouldHaveOnlyLimitedPermissions() {
        UUID boardId = UUID.randomUUID();
        when(boardRepository.findById(any())).thenReturn(board(new BoardEntity()));
        BoardUserEntity boardUserEntity = new BoardUserEntity();
        boardUserEntity.setRole(MEMBER.name());
        when(boardUserRepository.findUserBoard(boardId, 0)).thenReturn(boardUserEntity);
        assertTrue(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_VIEW));
        assertFalse(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_EDIT));
        assertFalse(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_USER_ROLE_EDIT));
        assertTrue(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_ENTRY_EDIT));
    }

    @DisplayName("hasGrants should have always permissions if OWNER")
    @Test
    void test_hasGrant_owner_shouldHaveAllGrants() {
        UUID boardId = UUID.randomUUID();
        when(boardRepository.findById(any())).thenReturn(board(new BoardEntity()));
        BoardUserEntity boardUserEntity = new BoardUserEntity();
        boardUserEntity.setRole(OWNER.name());
        when(boardUserRepository.findUserBoard(boardId, 0)).thenReturn(boardUserEntity);
        assertTrue(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_VIEW));
        assertTrue(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_EDIT));
        assertTrue(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_USER_ROLE_EDIT));
        assertTrue(service.hasGrant(0, boardId, BoardUserRole.BoardGrant.BOARD_ENTRY_EDIT));
    }
}
