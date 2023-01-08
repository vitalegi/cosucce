package it.vitalegi.budget.resource;

import it.vitalegi.budget.board.BoardEntryService;
import it.vitalegi.budget.board.BoardService;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.dto.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    BoardEntryService boardEntryService;

    @PutMapping()
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

    @PutMapping("/{boardId}")
    public BoardEntry addBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardEntryService.addBoardEntry(boardId, boardEntry);
    }

    @GetMapping("/{boardId}/entries")
    public List<BoardEntry> getBoardEntries(@PathVariable("boardId") UUID boardId) {
        return boardEntryService.getBoardEntries(boardId);
    }

    @GetMapping("/{boardId}/users")
    public List<User> getBoardUsers(@PathVariable("boardId") UUID boardId) {
        return null;
    }
}
