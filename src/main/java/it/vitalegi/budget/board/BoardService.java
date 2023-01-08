package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardService {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardUserService boardUserService;

    @Autowired
    UserService userService;

    @Autowired
    BoardMapper mapper;

    @Autowired
    BoardPermissionService boardPermissionService;

    @Transactional
    public Board addBoard(String name) {
        BoardEntity board = new BoardEntity();
        board.setName(name);
        board.setOwner(userService.getCurrentUserEntity());
        LocalDateTime now = LocalDateTime.now();
        board.setCreationDate(now);
        board.setLastUpdate(now);
        BoardEntity out = boardRepository.save(board);

        boardUserService.addBoardUserEntity(board, userService.getCurrentUserEntity(), BoardUserRole.OWNER);

        return mapper.map(out);
    }

    public Board getBoard(UUID id) {
        boardPermissionService.checkGrant(id, BoardGrant.VIEW);
        BoardEntity board = boardRepository.findById(id).get();
        return mapper.map(board);
    }

    public List<Board> getVisibleBoards() {
        Iterable<BoardEntity> boards = boardRepository.findByOwner_Id(userService.getCurrentUser().getId());
        return StreamSupport.stream(boards.spliterator(), false).map(mapper::map).collect(Collectors.toList());
    }
}
