package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Service
public class BoardService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BoardRepository repository;

    @Autowired
    BoardMapper mapper;

    @Autowired
    BoardPermissionService boardPermissionService;

    public Board addBoard(String name) {
        BoardEntity board = new BoardEntity();
        board.setName(name);
        board.setOwnerId(authenticationService.getId());
        LocalDateTime now = LocalDateTime.now();
        board.setCreationDate(now);
        board.setLastUpdate(now);
        BoardEntity out = repository.save(board);
        return mapper.map(out);
    }

    public List<Board> getVisibleBoards() {
        String userId = authenticationService.getId();
        Iterable<BoardEntity> boards = repository.findByOwnerId(userId);
        return StreamSupport.stream(boards.spliterator(), false).map(mapper::map).collect(Collectors.toList());
    }
}
