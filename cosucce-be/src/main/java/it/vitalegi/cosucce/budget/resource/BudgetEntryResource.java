package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardEntryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardEntryDto;
import it.vitalegi.cosucce.budget.model.BoardEntry;
import it.vitalegi.cosucce.budget.service.BoardEntryService;
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
@RequestMapping("budget/board/{boardId}/entry")
@AllArgsConstructor
public class BudgetEntryResource {

    BoardEntryService boardEntryService;
    AuthenticationService authenticationService;
    BudgetAuthorizationService budgetAuthorizationService;

    @PostMapping
    public BoardEntry addBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody AddBoardEntryDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        return boardEntryService.addBoardEntry(boardId, request.getAccountId(), request.getCategoryId(), request.getDescription(), request.getAmount(), userId());
    }

    @PutMapping("/{entryId}")
    public BoardEntry updateBoardEntry(@PathVariable("boardId") UUID boardId, @PathVariable("entryId") UUID entryId, @RequestBody UpdateBoardEntryDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        return boardEntryService.updateBoardEntry(boardId, entryId, request.getAccountId(), request.getCategoryId(), request.getDescription(), request.getAmount(), userId(), request.getVersion());
    }

    @DeleteMapping("/{entryId}")
    public BoardEntry deleteBoardEntry(@PathVariable("boardId") UUID boardId, @PathVariable("entryId") UUID entryId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        return boardEntryService.deleteBoardEntry(boardId, entryId);
    }

    @GetMapping
    public List<BoardEntry> getBoardEntries(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        return boardEntryService.getBoardEntries(boardId);
    }

    UUID userId() {
        return authenticationService.identity().getId();
    }
}
