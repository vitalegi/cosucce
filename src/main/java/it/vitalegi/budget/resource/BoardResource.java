package it.vitalegi.budget.resource;

import it.vitalegi.budget.board.BoardService;
import it.vitalegi.budget.board.analysis.dto.MonthlyUserAnalysis;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping()
    public Board addBoard(@RequestBody Board board) {
        return boardService.addBoard(board.getName());
    }

    @GetMapping("/{boardId}")
    public Board getBoard(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoard(boardId);
    }

    @GetMapping()
    public List<Board> getBoards() {
        return boardService.getVisibleBoards();
    }

    @PostMapping("/{boardId}/entry")
    public BoardEntry addBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardService.addBoardEntry(boardId, boardEntry);
    }

    @PostMapping("/{boardId}/user/{userId}")
    public BoardUser addBoardUser(@PathVariable("boardId") UUID boardId, @PathVariable("userId") long userId, @RequestBody BoardUser boardUser) {
        BoardEntity board = boardService.getBoardEntity(boardId);
        UserEntity user = userService.getUserEntity(userId);
        return boardService.addBoardUser(board, user, boardUser.getRole());
    }

    @GetMapping("/{boardId}/entries")
    public List<BoardEntry> getBoardEntries(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardEntries(boardId);
    }

    @GetMapping("/{boardId}/users")
    public List<BoardUser> getBoardUsers(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardUsers(boardId);
    }


    @GetMapping("/{boardId}/categories")
    public List<String> getBoardCategories(@PathVariable("boardId") UUID boardId) {
        return boardService.getCategories(boardId);
    }

    @PostMapping("/{boardId}/split")
    public BoardSplit addBoardSplit(@PathVariable("boardId") UUID boardId, @RequestBody BoardSplit split) {
        return boardService.addBoardSplit(boardId, split.getUserId(), split.getFromYear(), split.getFromMonth(), split.getToYear(), split.getToMonth(), split.getValue1());
    }

    @GetMapping("/{boardId}/splits")
    public List<BoardSplit> getBoardSplits(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardSplits(boardId);
    }

    @GetMapping("/{boardId}/analysis/month-user")
    public List<MonthlyUserAnalysis> getBoardAnalysisMonthUser(@PathVariable("boardId") UUID boardId) {
        return boardService.getBoardAnalysisByMonthUser(boardId);
    }
}
