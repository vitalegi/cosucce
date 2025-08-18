package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardDto;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.service.BoardService;
import it.vitalegi.cosucce.budget.service.BudgetAuthorizationService;
import it.vitalegi.cosucce.iam.model.Permission;
import it.vitalegi.cosucce.iam.service.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BudgetBoardResource {

    BoardService boardService;
    AuthenticationService authenticationService;
    BudgetAuthorizationService budgetAuthorizationService;

    @PostMapping
    public Board addBoard(@RequestBody AddBoardDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        var out = boardService.addBoard(request.getBoardId(), request.getName(), request.getEtag(), userId());
        log.info("action=ADD, board={}, name={}, etag={}", out.getBoardId(), out.getName(), out.getEtag());
        return out;
    }

    @PutMapping("/{boardId}")
    public Board updateBoard(@PathVariable("boardId") UUID boardId, @RequestBody UpdateBoardDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        var out = boardService.updateBoard(boardId, request.getName(), request.getEtag(), request.getNewETag());
        log.info("action=UPDATE, board={}, name={}, etag={}", out.getBoardId(), out.getName(), out.getEtag());
        return out;
    }

    @DeleteMapping("/{boardId}")
    public Board deleteBoard(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        var out = boardService.deleteBoard(boardId);
        log.info("action=DELETE, board={}, name={}, etag={}", out.getBoardId(), out.getName(), out.getEtag());
        return out;
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
