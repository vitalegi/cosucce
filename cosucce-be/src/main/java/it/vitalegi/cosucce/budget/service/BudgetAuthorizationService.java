package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.constants.BoardUserRole;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import it.vitalegi.cosucce.budget.exception.UnauthorizedBoardAccessException;
import it.vitalegi.cosucce.budget.repository.BoardUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class BudgetAuthorizationService {

    BoardUserRepository boardUserRepository;
    Map<BoardUserRole, Set<BoardUserPermission>> rbac;

    public BudgetAuthorizationService(BoardUserRepository boardUserRepository) {
        this.boardUserRepository = boardUserRepository;
        rbac = new HashMap<>();
        rbac.put(BoardUserRole.OWNER, Set.of(BoardUserPermission.ADMIN, BoardUserPermission.WRITE, BoardUserPermission.READ));
        rbac.put(BoardUserRole.MEMBER, Set.of(BoardUserPermission.WRITE, BoardUserPermission.READ));
    }

    public void checkPermission(UUID boardId, UUID userId, BoardUserPermission permission) {
        var permissions = getPermissions(boardId, userId);
        if (permissions.stream().noneMatch(permission::equals)) {
            throw new UnauthorizedBoardAccessException(boardId, permissions, permission);
        }
    }

    protected List<BoardUserPermission> getPermissions(UUID boardId, UUID userId) {
        var opt = boardUserRepository.findById(new BoardUserId(boardId, userId));
        if (opt.isEmpty()) {
            return List.of();
        }
        var role = role(opt.get().getRole());
        return rbac.get(role).stream().toList();
    }

    protected BoardUserRole role(String role) {
        return BoardUserRole.valueOf(role);
    }
}
