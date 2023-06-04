package it.vitalegi.cosucce.board.service;

import it.vitalegi.cosucce.board.constant.BoardUserRole;
import it.vitalegi.cosucce.board.entity.BoardEntity;
import it.vitalegi.cosucce.board.entity.BoardUserEntity;
import it.vitalegi.cosucce.board.repository.BoardRepository;
import it.vitalegi.cosucce.board.repository.BoardUserRepository;
import it.vitalegi.cosucce.user.entity.UserEntity;
import it.vitalegi.cosucce.user.service.UserService;
import it.vitalegi.exception.PermissionException;
import it.vitalegi.metrics.Performance;
import it.vitalegi.metrics.Type;
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

import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_DELETE;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_EDIT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_ENTRY_EDIT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_ENTRY_IMPORT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_MANAGE_MEMBER;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_USER_ROLE_EDIT;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.BoardGrant.BOARD_VIEW;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.MEMBER;
import static it.vitalegi.cosucce.board.constant.BoardUserRole.OWNER;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardPermissionService {

    protected static final Map<BoardUserRole, List<BoardUserRole.BoardGrant>> RBAC;

    static {
        RBAC = new HashMap<>();
        RBAC.put(OWNER, Arrays.asList(BOARD_VIEW, BOARD_EDIT, BOARD_ENTRY_EDIT, BOARD_USER_ROLE_EDIT,
                BOARD_MANAGE_MEMBER, BOARD_ENTRY_IMPORT, BOARD_DELETE));
        RBAC.put(MEMBER, Arrays.asList(BOARD_VIEW, BOARD_ENTRY_EDIT));
    }

    @Autowired
    UserService userService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardUserRepository boardUserRepository;


    public void checkGrant(UUID boardId, BoardUserRole.BoardGrant grant) {
        if (!hasGrant(userService.getCurrentUser().getId(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }

    public void checkGrant(UserEntity user, UUID boardId, BoardUserRole.BoardGrant grant) {
        if (!hasGrant(user.getId(), boardId, grant)) {
            throw new PermissionException("board", boardId.toString(), grant.name());
        }
    }

    public List<BoardUserRole.BoardGrant> getGrants(UUID boardId) {
        long userId = userService.getCurrentUser().getId();
        return getGrants(userId, boardId);
    }

    public List<BoardUserRole.BoardGrant> getGrants(long userId, UUID boardId) {
        BoardUserEntity role = boardUserRepository.findUserBoard(boardId, userId);
        if (role != null) {
            BoardUserRole userRole = BoardUserRole.valueOf(role.getRole());
            return RBAC.get(userRole);
        }
        return Collections.emptyList();
    }

    protected boolean hasGrant(long userId, UUID boardId, BoardUserRole.BoardGrant grant) {
        Optional<BoardEntity> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            return false;
        }
        return getGrants(userId, boardId).contains(grant);
    }
}
