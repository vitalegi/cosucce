package it.vitalegi.budget.board.service;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.exception.PermissionException;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.entity.UserEntity;
import it.vitalegi.budget.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static it.vitalegi.budget.auth.BoardGrant.BOARD_EDIT;
import static it.vitalegi.budget.auth.BoardGrant.BOARD_ENTRY_EDIT;
import static it.vitalegi.budget.auth.BoardGrant.BOARD_ENTRY_IMPORT;
import static it.vitalegi.budget.auth.BoardGrant.BOARD_MANAGE_MEMBER;
import static it.vitalegi.budget.auth.BoardGrant.BOARD_USER_ROLE_EDIT;
import static it.vitalegi.budget.auth.BoardGrant.BOARD_VIEW;
import static it.vitalegi.budget.board.constant.BoardUserRole.MEMBER;
import static it.vitalegi.budget.board.constant.BoardUserRole.OWNER;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardPermissionService {

    protected static final Map<BoardUserRole, List<BoardGrant>> RBAC;

    static {
        RBAC = new HashMap<>();
        RBAC.put(OWNER, Arrays.asList(BOARD_VIEW, BOARD_EDIT, BOARD_ENTRY_EDIT, BOARD_USER_ROLE_EDIT,
                BOARD_MANAGE_MEMBER, BOARD_ENTRY_IMPORT));
        RBAC.put(MEMBER, Arrays.asList(BOARD_VIEW, BOARD_ENTRY_EDIT));
    }

    @Autowired
    UserService userService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardUserRepository boardUserRepository;


    public void checkGrant(UUID boardId, BoardGrant grant) {
        if (!hasGrant(userService.getCurrentUser().getId(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }

    public void checkGrant(UserEntity user, UUID boardId, BoardGrant grant) {
        if (!hasGrant(user.getId(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }

    public List<BoardGrant> getGrants(UUID boardId) {
        long userId = userService.getCurrentUser().getId();
        return getGrants(userId, boardId);
    }

    public List<BoardGrant> getGrants(long userId, UUID boardId) {
        BoardUserEntity role = boardUserRepository.findUserBoard(boardId, userId);
        if (role != null) {
            BoardUserRole userRole = BoardUserRole.valueOf(role.getRole());
            return RBAC.get(userRole);
        }
        return Collections.emptyList();
    }

    protected boolean hasGrant(long userId, UUID boardId, BoardGrant grant) {
        Optional<BoardEntity> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            return false;
        }
        return getGrants(userId, boardId).contains(grant);
    }
}
