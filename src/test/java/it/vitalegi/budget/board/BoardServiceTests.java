package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BoardServiceTests {

    final static String BOARD = "board";
    final static String USER_ID = "user1";

    BoardService service;
    AuthenticationService authenticationService;

    BoardRepository repository;

    BoardMapper mapper;

    @BeforeEach
    void initTest() {
        service = new BoardService();

        authenticationService = mock(AuthenticationService.class);
        when(authenticationService.getId()).thenReturn(USER_ID);

        service = new BoardService();
        service.authenticationService = authenticationService;

        repository = mock(BoardRepository.class);
        service.repository = repository;

        mapper = mock(BoardMapper.class);
        service.mapper = mapper;
    }

    @Test
    void test_addBoard_shouldCreateBoard() {
        ArgumentCaptor<BoardEntity> entity = ArgumentCaptor.forClass(BoardEntity.class);
        BoardEntity saveMock = BoardMock.newBoardEntity(BOARD, USER_ID);
        Board mapperMock = BoardMock.emptyBoard();
        when(repository.save(any())).thenReturn(saveMock);
        when(mapper.map(eq(saveMock))).thenReturn(mapperMock);

        Board board = service.addBoard(BOARD);

        // verify that repository is called properly
        verify(repository).save(entity.capture());
        BoardEntity repositoryInput = entity.getValue();
        assertEquals(BOARD, repositoryInput.getName());
        assertEquals(USER_ID, repositoryInput.getOwnerId());
        assertNull(repositoryInput.getId());
        assertNotNull(repositoryInput.getCreationDate());
        assertNotNull(repositoryInput.getLastUpdate());
        assertTrue(repositoryInput.getCreationDate().equals(repositoryInput.getLastUpdate()));

        // verify that output is correct
        assertEquals(mapperMock, board);
    }

}
