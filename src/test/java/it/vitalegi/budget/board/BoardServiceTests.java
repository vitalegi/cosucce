package it.vitalegi.budget.board;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardServiceTests {

    final static String BOARD = "board";
    final static long USER_ID = 1;

    BoardService service;

    BoardRepository repository;

    BoardMapper mapper;

    UserService userService;

    BoardUserRepository boardUserRepository;

    @BeforeEach
    void initTest() {
        service = new BoardService();

        repository = mock(BoardRepository.class);
        service.boardRepository = repository;

        mapper = mock(BoardMapper.class);
        service.mapper = mapper;

        userService = mock(UserService.class);
        service.userService = userService;

        boardUserRepository = mock(BoardUserRepository.class);
        service.boardUserRepository = boardUserRepository;
    }

    @Test
    void test_addBoard_shouldCreateBoard() {
        ArgumentCaptor<BoardEntity> entity = ArgumentCaptor.forClass(BoardEntity.class);
        BoardEntity saveMock = BoardMock.newBoardEntity(BOARD, USER_ID);
        Board mapperMock = BoardMock.emptyBoard();
        when(repository.save(any())).thenReturn(saveMock);
        when(mapper.map(eq(saveMock))).thenReturn(mapperMock);
        UserEntity ownerEntity = new UserEntity();
        ownerEntity.setId(USER_ID);
        when(userService.getCurrentUserEntity()).thenReturn(ownerEntity);
        Board board = service.addBoard(BOARD);

        // verify that repository is called properly
        verify(repository).save(entity.capture());
        BoardEntity repositoryInput = entity.getValue();
        assertEquals(BOARD, repositoryInput.getName());
        assertNull(repositoryInput.getId());
        assertNotNull(repositoryInput.getCreationDate());
        assertNotNull(repositoryInput.getLastUpdate());
        assertEquals(repositoryInput.getCreationDate(), repositoryInput.getLastUpdate());

        // verify that output is correct
        assertEquals(mapperMock, board);
    }

}
