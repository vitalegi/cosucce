package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.dto.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardUserService {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardMapper mapper;
    @Autowired
    BoardPermissionService boardPermissionService;

    public List<User> getBoardUsers(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.VIEW);
        BoardEntity board = boardRepository.findById(boardId).get();
        return null;
    }
}
