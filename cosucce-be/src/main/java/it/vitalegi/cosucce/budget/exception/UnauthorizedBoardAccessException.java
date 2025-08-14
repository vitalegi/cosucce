package it.vitalegi.cosucce.budget.exception;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class UnauthorizedBoardAccessException extends RuntimeException {
    UUID boardId;
    List<BoardUserPermission> userPermissions;
    BoardUserPermission missingPermission;

    public UnauthorizedBoardAccessException(UUID boardId, List<BoardUserPermission> userPermissions, BoardUserPermission missingPermission) {
        super("Missing permission " + missingPermission + " on board: " + boardId + ". Available: " + userPermissions);
        this.boardId = boardId;
        this.userPermissions = userPermissions;
        this.missingPermission = missingPermission;
    }
}
