package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardAccountDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardAccountDto;
import it.vitalegi.cosucce.budget.model.BoardAccount;
import it.vitalegi.cosucce.budget.service.BoardAccountService;
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
@RequestMapping("budget/board/{boardId}/account")
@AllArgsConstructor
@Slf4j
public class BudgetAccountResource {

    BoardAccountService boardAccountService;
    AuthenticationService authenticationService;
    BudgetAuthorizationService budgetAuthorizationService;

    @PostMapping
    public BoardAccount addBoardAccount(@PathVariable("boardId") UUID boardId, @RequestBody AddBoardAccountDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        var out = boardAccountService.addBoardAccount(boardId, request.getAccountId(), request.getLabel(), request.getIcon());
        log.info("action=ADD, board={}, account={}, label={}, version={}", boardId, out.getAccountId(), out.getLabel(), out.getVersion());
        return out;
    }

    @PutMapping("/{accountId}")
    public BoardAccount updateBoardAccount(@PathVariable("boardId") UUID boardId, @PathVariable("accountId") UUID accountId, @RequestBody UpdateBoardAccountDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        var out = boardAccountService.updateBoardAccount(boardId, accountId, request.getLabel(), request.getIcon(), request.isEnabled(), request.getVersion());
        log.info("action=UPDATE, board={}, account={}, label={}, version={}", boardId, out.getAccountId(), out.getLabel(), out.getVersion());
        return out;
    }

    @DeleteMapping("/{accountId}")
    public BoardAccount deleteBoardAccount(@PathVariable("boardId") UUID boardId, @PathVariable("accountId") UUID accountId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        var out = boardAccountService.deleteBoardAccount(boardId, accountId);
        log.info("action=DELETE, board={}, account={}, label={}, version={}", boardId, out.getAccountId(), out.getLabel(), out.getVersion());
        return out;
    }

    @GetMapping
    public List<BoardAccount> getBoardAccounts(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        return boardAccountService.getBoardAccounts(boardId);
    }

    UUID userId() {
        return authenticationService.identity().getId();
    }
}
