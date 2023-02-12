package it.vitalegi.budget.resource;

import io.swagger.v3.oas.annotations.Operation;
import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.dto.AddBoard;
import it.vitalegi.budget.board.dto.AddBoardEntries;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardInvite;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.dto.analysis.MonthlyAnalysis;
import it.vitalegi.budget.board.dto.analysis.MonthlyUserAnalysis;
import it.vitalegi.budget.board.service.BoardPermissionService;
import it.vitalegi.budget.board.service.BoardService;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

@Log4j2
@RestController
@RequestMapping("/board")
@Performance(Type.ENDPOINT)
public class BoardResource {

    @Autowired
    BoardService boardService;
    @Autowired
    BoardPermissionService boardPermissionService;


    @Operation(summary = "Create a new board")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Board addBoard(@RequestBody AddBoard board) {
        return boardService.addBoard(board.getName());
    }

    @Operation(summary = "Add board entries")
    @PostMapping(path = "/{boardId}/entries", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public List<BoardEntry> addBoardEntries(@PathVariable("boardId") UUID boardId,
                                            @RequestBody AddBoardEntries request) {
        return boardService.addBoardEntries(boardId, request.getEntries());
    }

    @Operation(summary = "Add new entry to board")
    @PostMapping(path = "/{boardId}/entry", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardEntry addBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardService.addBoardEntry(boardId, boardEntry);
    }

    @Operation(summary = "Create new board invite")
    @PostMapping(path = "/{boardId}/invite", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardInvite addBoardInvite(@PathVariable("boardId") UUID boardId) {
        return boardService.addBoardInvite(boardId);
    }

    @Operation(summary = "Add new split configuration")
    @PostMapping(path = "/{boardId}/split", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardSplit addBoardSplit(@PathVariable("boardId") UUID boardId, @RequestBody BoardSplit split) {
        return boardService.addBoardSplit(boardId, split.getUserId(), split.getFromYear(), split.getFromMonth(),
                split.getToYear(), split.getToMonth(), split.getValue1());
    }

    @Operation(summary = "Delete board")
    @DeleteMapping(path = "/{boardId}")
    public void deleteBoard(@PathVariable("boardId") UUID boardId) {
        boardService.deleteBoard(boardId);
    }

    @Operation(summary = "Delete board entry")
    @DeleteMapping(path = "/{boardId}/entry/{boardEntryId}")
    public void deleteBoardEntry(@PathVariable("boardId") UUID boardId,
                                 @PathVariable("boardEntryId") UUID boardEntryId) {
        boardService.deleteBoardEntry(boardId, boardEntryId);
    }

    @Operation(summary = "Delete board split")
    @DeleteMapping(path = "/{boardId}/split/{boardSplitId}")
    public void deleteBoardSplit(@PathVariable("boardId") UUID boardId,
                                 @PathVariable("boardSplitId") UUID boardSplitId) {
        boardService.deleteBoardSplit(boardId, boardSplitId);
    }

    @Operation(summary = "Retrieve board")
    @GetMapping(path = "/{boardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Board getBoard(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoard(boardId);
    }

    @Operation(summary = "Retrieve board analysis, by month")
    @GetMapping(path = "/{boardId}/analysis/month", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MonthlyAnalysis> getBoardAnalysisMonth(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardAnalysisByMonth(boardId);
    }

    @Operation(summary = "Retrieve board analysis, by month and user")
    @GetMapping(path = "/{boardId}/analysis/month-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MonthlyUserAnalysis> getBoardAnalysisMonthUser(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardAnalysisByMonthUser(boardId);
    }

    @Operation(summary = "Retrieve board's categories")
    @GetMapping(path = "/{boardId}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getBoardCategories(@PathVariable("boardId") UUID boardId) {
        return boardService.getCategories(boardId);
    }

    @Operation(summary = "Retrieve board's entries")
    @GetMapping(path = "/{boardId}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardEntry> getBoardEntries(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardEntries(boardId);
    }

    @Operation(summary = "Retrieve board entry")
    @GetMapping(path = "/{boardId}/entry/{boardEntryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardEntry getBoardEntry(@PathVariable("boardId") UUID boardId,
                                    @PathVariable("boardEntryId") UUID boardEntryId) {
        return boardService.getBoardEntry(boardId, boardEntryId);
    }

    @Operation(summary = "Retrieve board's split configurations")
    @GetMapping(path = "/{boardId}/splits", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardSplit> getBoardSplits(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardSplits(boardId);
    }

    @Operation(summary = "Retrieve board's users")
    @GetMapping(path = "/{boardId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardUser> getBoardUsers(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardUsers(boardId);
    }

    @Operation(summary = "Retrieve visible boards")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Board> getBoards() {
        return boardService.getVisibleBoards();
    }

    @Operation(summary = "Get permissions of the current user")
    @GetMapping(path = "/{boardId}/grants", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardGrant> getGrants(@PathVariable("boardId") UUID boardId) {
        return boardPermissionService.getGrants(boardId);
    }

    @Operation(summary = "Update existing entry in board")
    @PutMapping(path = "/{boardId}/entry", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardEntry updateBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardService.updateBoardEntry(boardId, boardEntry);
    }

    @Operation(summary = "Changes the name of the board")
    @PutMapping(path = "/{boardId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public Board updateBoardName(@PathVariable("boardId") UUID boardId, @RequestBody AddBoard board) {
        return boardService.updateBoard(boardId, board.getName());
    }

    @Operation(summary = "Update existing split in board")
    @PutMapping(path = "/{boardId}/split", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardSplit updateBoardSplit(@PathVariable("boardId") UUID boardId, @RequestBody BoardSplit boardSplit) {
        return boardService.updateBoardSplit(boardId, boardSplit);
    }

    @Operation(summary = "Create new board invite")
    @GetMapping(path = "/{boardId}/invite/{invite}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void useBoardInvite(@PathVariable("boardId") UUID boardId, @PathVariable("invite") UUID invite) {
        boardService.useBoardInvite(boardId, invite);
    }
}
