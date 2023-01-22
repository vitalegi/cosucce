package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardSplitEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.board.repository.BoardSplitRepository;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardServiceTests {

    final static String BOARD = "board";
    static long USER_ID = 1;

    static long RANDOM_ID = 0;

    BoardService service;

    BoardRepository repository;

    BoardMapper mapper;

    UserService userService;

    BoardUserRepository boardUserRepository;
    BoardSplitRepository boardSplitRepository;
    BoardPermissionService boardPermissionService;

    @BeforeEach
    void initTest() {
        service = new BoardService();

        repository = mock(BoardRepository.class);
        service.boardRepository = repository;

        mapper = mock(BoardMapper.class);
        when(mapper.map(any(BoardSplitEntity.class))).thenCallRealMethod();
        service.mapper = mapper;

        userService = mock(UserService.class);
        service.userService = userService;

        boardUserRepository = mock(BoardUserRepository.class);
        service.boardUserRepository = boardUserRepository;

        boardSplitRepository = mock(BoardSplitRepository.class);
        service.boardSplitRepository = boardSplitRepository;

        boardPermissionService = mock(BoardPermissionService.class);
        service.boardPermissionService = boardPermissionService;
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


    @DisplayName("getBoardSplits, with splits available, should return splits")
    @Test
    void test_getBoardSplits_splitsAvailable_shouldReturnExistingSplits() {
        UUID boardId = UUID.randomUUID();
        List<BoardSplitEntity> entities = new ArrayList<>();
        BoardEntity board = randomBoard(boardId);
        BoardSplitEntity s1 = boardSplit(board, new BigDecimal("0.30"));
        BoardSplitEntity s2 = boardSplit(board, new BigDecimal("0.70"));
        entities.add(s1);
        entities.add(s2);
        when(boardSplitRepository.findByBoardId(boardId)).thenReturn(entities);
        List<BoardSplit> out = service.getBoardSplits(boardId);
        assertEquals(s1.getValue1(), out.stream().filter(o -> o.getUserId() == s1.getUser().getId()).findFirst().orElse(null).getValue1());
        assertEquals(s2.getValue1(), out.stream().filter(o -> o.getUserId() == s2.getUser().getId()).findFirst().orElse(null).getValue1());
    }

    @DisplayName("getBoardSplits, if there are no splits available, create default ones with 1/n ratio")
    @Test
    void test_getBoardSplits_noSplits_shouldCreateDefaultValues() {
        UUID boardId = UUID.randomUUID();
        when(boardSplitRepository.findByBoardId(boardId)).thenReturn(new ArrayList<>());

        BoardEntity board = randomBoard(boardId);
        List<BoardUserEntity> boardUsers = new ArrayList<>();
        boardUsers.add(boardUser(board));
        boardUsers.add(boardUser(board));
        boardUsers.add(boardUser(board));
        when(boardUserRepository.findByBoard_Id(boardId)).thenReturn(boardUsers);

        List<BoardSplit> out = service.getBoardSplits(boardId);
        assertEquals(3, out.size());

        boardUsers.forEach(boardUser -> {
            List<Long> ids = out.stream().map(BoardSplit::getUserId).collect(Collectors.toList());
            assertTrue(out.stream().anyMatch(bs -> bs.getUserId() == boardUser.getUser().getId()), "Output doesn't contain an entry for user " + boardUser.getUser().getId() + ": " + ids);
        });
        assertEquals(new BigDecimal("0.34"), out.get(0).getValue1());
        assertEquals(new BigDecimal("0.33"), out.get(1).getValue1());
        assertEquals(new BigDecimal("0.33"), out.get(2).getValue1());
    }

    protected BoardSplitEntity boardSplit(BoardEntity board, BigDecimal ratio) {
        BoardSplitEntity entity = new BoardSplitEntity();
        entity.setBoard(board);
        entity.setUser(randomUser());
        entity.setValue1(ratio);
        return entity;
    }

    protected BoardEntity randomBoard(UUID boardId) {
        BoardEntity entity = new BoardEntity();
        entity.setName("name " + boardId);
        entity.setId(boardId);
        return entity;
    }

    protected BoardUserEntity boardUser(BoardEntity board) {
        BoardUserEntity entity = new BoardUserEntity();
        entity.setId(++RANDOM_ID);
        entity.setUser(randomUser());
        entity.setBoard(board);
        entity.setRole(BoardUserRole.MEMBER.name());
        return entity;
    }

    protected UserEntity randomUser() {
        UserEntity user = new UserEntity();
        user.setId(++USER_ID);
        user.setUsername("username_" + user.getId());
        user.setUid("uid_" + user.getId());
        return user;
    }
}
