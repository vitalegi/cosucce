package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.exception.PermissionException;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.entity.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static it.vitalegi.budget.auth.BoardGrant.EDIT_BOARD;
import static it.vitalegi.budget.auth.BoardGrant.EDIT_BOARD_ENTRY;
import static it.vitalegi.budget.auth.BoardGrant.EDIT_BOARD_USER_ROLE;
import static it.vitalegi.budget.auth.BoardGrant.VIEW;
import static it.vitalegi.budget.board.constant.BoardUserRole.MEMBER;
import static it.vitalegi.budget.board.constant.BoardUserRole.OWNER;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardPermissionService {

    protected static final Map<BoardUserRole, List<BoardGrant>> RBAC;

    static {
        RBAC = new HashMap<>();
        RBAC.put(OWNER, Arrays.asList(VIEW, EDIT_BOARD, EDIT_BOARD_ENTRY, EDIT_BOARD_USER_ROLE));
        RBAC.put(MEMBER, Arrays.asList(VIEW, EDIT_BOARD_ENTRY));
    }

    @Autowired
    UserService userService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardUserRepository boardUserRepository;


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

    public boolean hasGrant(long userId, UUID boardId, BoardGrant grant) {
        Optional<BoardEntity> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            return false;
        }
        BoardUserEntity role = boardUserRepository.findUserBoard(boardId, userId);
        if (role != null) {
            BoardUserRole userRole = BoardUserRole.valueOf(role.getRole());
            return hasGrant(userRole, grant);
        }
        return false;
    }

    protected boolean hasGrant(BoardUserRole role, BoardGrant permission) {
        return RBAC.get(role).contains(permission);
    }
}
