package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.exception.PermissionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class BoardPermissionService {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    BoardRepository repository;

    public boolean hasGrant(String userId, UUID boardId, BoardGrant grant) {
        Optional<BoardEntity> board = repository.findById(boardId);
        if (board.isEmpty()) {
            return false;
        }
        String ownerId = board.get().getOwnerId();
        if (userId.equals(ownerId)) {
            return true;
        }
        return false;
    }

    public void checkGrant(UUID boardId, BoardGrant grant) {
        if (!hasGrant(authenticationService.getId(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }


    public void checkGrant(String userId, UUID boardId, BoardGrant grant) {
        if (!hasGrant(userId, boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }


}
