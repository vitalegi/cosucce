package it.vitalegi.budget.resource;

import io.swagger.v3.oas.annotations.Operation;
import it.vitalegi.budget.board.BoardService;
import it.vitalegi.budget.board.analysis.dto.MonthlyUserAnalysis;
import it.vitalegi.budget.board.dto.AddBoard;
import it.vitalegi.budget.board.dto.AddBoardUser;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.entity.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    UserService userService;

    @Operation(summary = "Create a new board")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Board addBoard(@RequestBody AddBoard board) {
        return boardService.addBoard(board.getName());
    }

    @Operation(summary = "Retrieve board")
    @GetMapping(path = "/{boardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Board getBoard(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoard(boardId);
    }

    @Operation(summary = "Retrieve visible boards")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Board> getBoards() {
        return boardService.getVisibleBoards();
    }

    @Operation(summary = "Add new entry to board")
    @PostMapping(path = "/{boardId}/entry", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardEntry addBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardService.addBoardEntry(boardId, boardEntry);
    }

    @Operation(summary = "Update existing entry in board")
    @PutMapping(path = "/{boardId}/entry", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardEntry updateBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardService.updateBoardEntry(boardId, boardEntry);
    }

    @Operation(summary = "Add user to board")
    @PostMapping(path = "/{boardId}/user/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardUser addBoardUser(@PathVariable("boardId") UUID boardId, @PathVariable("userId") long userId,
                                  @RequestBody AddBoardUser boardUser) {
        BoardEntity board = boardService.getBoardEntity(boardId);
        UserEntity user = userService.getUserEntity(userId);
        return boardService.addBoardUser(board, user, boardUser.getRole());
    }

    @Operation(summary = "Retrieve board's entries")
    @GetMapping(path = "/{boardId}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardEntry> getBoardEntries(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardEntries(boardId);
    }

    @Operation(summary = "Retrieve board's entries")
    @GetMapping(path = "/{boardId}/entry/{boardEntryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardEntry getBoardEntry(@PathVariable("boardId") UUID boardId,@PathVariable("boardEntryId") UUID boardEntryId) {
        return boardService.getBoardEntry(boardId, boardEntryId);
    }

    @Operation(summary = "Retrieve board's users")
    @GetMapping(path = "/{boardId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardUser> getBoardUsers(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardUsers(boardId);
    }

    @Operation(summary = "Retrieve board's categories")
    @GetMapping(path = "/{boardId}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getBoardCategories(@PathVariable("boardId") UUID boardId) {
        return boardService.getCategories(boardId);
    }

    @Operation(summary = "Add new split configuration")
    @PostMapping(path = "/{boardId}/split", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public BoardSplit addBoardSplit(@PathVariable("boardId") UUID boardId, @RequestBody BoardSplit split) {
        return boardService.addBoardSplit(boardId, split.getUserId(), split.getFromYear(), split.getFromMonth(),
                split.getToYear(), split.getToMonth(), split.getValue1());
    }

    @Operation(summary = "Retrieve board's split configurations")
    @GetMapping(path = "/{boardId}/splits", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardSplit> getBoardSplits(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardSplits(boardId);
    }

    @Operation(summary = "Retrieve board analysis")
    @GetMapping(path = "/{boardId}/analysis/month-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MonthlyUserAnalysis> getBoardAnalysisMonthUser(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardAnalysisByMonthUser(boardId);
    }
}
