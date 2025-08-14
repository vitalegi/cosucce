package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.UpdateBoard;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.service.BoardService;
import it.vitalegi.cosucce.budget.service.BudgetAuthorizationService;
import it.vitalegi.cosucce.iam.model.Permission;
import it.vitalegi.cosucce.iam.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("budget/board")
@AllArgsConstructor
public class BudgetBoardResource {

    BoardService boardService;
    AuthenticationService authenticationService;
    BudgetAuthorizationService budgetAuthorizationService;

    @PostMapping
    public Board addBoard() {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        return boardService.addBoard(userId());
    }

    @PutMapping("/{boardId}")
    public Board updateBoard(@PathVariable("boardId") UUID boardId, @RequestBody UpdateBoard request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        return boardService.updateBoard(boardId, request.getName());
    }

    @DeleteMapping("/{boardId}")
    public Board deleteBoard(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        return boardService.deleteBoard(boardId);
    }

    @GetMapping
    public List<Board> getBoards() {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        return boardService.getVisibleBoards(userId());
    }

    UUID userId() {
        return authenticationService.identity().getId();
    }
}
