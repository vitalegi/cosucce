package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.exception.PermissionException;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.entity.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardPermissionService {

    @Autowired
    UserService userService;
    @Autowired
    BoardRepository boardRepository;

    public boolean hasGrant(long userId, UUID boardId, BoardGrant grant) {
        Optional<BoardEntity> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            return false;
        }
        UserEntity owner = board.get().getOwner();
        if (owner.getId() == userId) {
            return true;
        }
        return false;
    }
    public boolean hasGrant(User user, UUID boardId, BoardGrant grant) {
        return hasGrant(user.getId(), boardId, grant);
    }

    public void checkGrant(UUID boardId, BoardGrant grant) {
        if (!hasGrant(userService.getCurrentUser(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }

    public void checkGrant(UserEntity user, UUID boardId, BoardGrant grant) {
        if (!hasGrant(user.getId(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }
}
