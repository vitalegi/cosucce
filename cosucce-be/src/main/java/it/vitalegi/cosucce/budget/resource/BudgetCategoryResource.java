package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardCategoryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardCategoryDto;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.service.BoardCategoryService;
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
@RequestMapping("budget/board/{boardId}/category")
@AllArgsConstructor
@Slf4j
public class BudgetCategoryResource {

    BoardCategoryService boardCategoryService;
    AuthenticationService authenticationService;
    BudgetAuthorizationService budgetAuthorizationService;

    @PostMapping
    public BoardCategory addBoardCategory(@PathVariable("boardId") UUID boardId, @RequestBody AddBoardCategoryDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        var out = boardCategoryService.addBoardCategory(boardId, request.getCategoryId(), request.getLabel(), request.getIcon(),request.getEtag());
        log.info("action=ADD, board={}, category={}, label={}, etag={}", boardId, out.getCategoryId(), out.getLabel(), out.getEtag());
        return out;
    }

    @PutMapping("/{categoryId}")
    public BoardCategory updateBoardCategory(@PathVariable("boardId") UUID boardId, @PathVariable("categoryId") UUID categoryId, @RequestBody UpdateBoardCategoryDto request) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        var out = boardCategoryService.updateBoardCategory(boardId, categoryId, request.getLabel(), request.getIcon(), request.isEnabled(), request.getEtag(), request.getNewETag());
        log.info("action=UPDATE, board={}, category={}, label={}, etag={}", boardId, out.getCategoryId(), out.getLabel(), out.getEtag());
        return out;
    }

    @DeleteMapping("/{categoryId}")
    public BoardCategory deleteBoardCategory(@PathVariable("boardId") UUID boardId, @PathVariable("categoryId") UUID categoryId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        budgetAuthorizationService.checkPermission(boardId, userId(), BoardUserPermission.ADMIN);
        var out = boardCategoryService.deleteBoardCategory(boardId, categoryId);
        log.info("action=DELETE, board={}, category={}, label={}, etag={}", boardId, out.getCategoryId(), out.getLabel(), out.getEtag());
        return out;
    }

    @GetMapping
    public List<BoardCategory> getBoardCategories(@PathVariable("boardId") UUID boardId) {
        authenticationService.checkPermission(Permission.BUDGET_ACCESS);
        return boardCategoryService.getBoardCategories(boardId);
    }

    UUID userId() {
        return authenticationService.identity().getId();
    }
}
