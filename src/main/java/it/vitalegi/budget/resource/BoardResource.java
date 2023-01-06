package it.vitalegi.budget.resource;

import it.vitalegi.budget.board.BoardEntryService;
import it.vitalegi.budget.board.BoardService;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping()
    public List<Board> getBoards() {
        return boardService.getVisibleBoards();
    }

    @PutMapping("/{boardId}")
    public BoardEntry addBoardEntry(@PathVariable("boardId") UUID boardId, @RequestBody BoardEntry boardEntry) {
        return boardEntryService.addBoardEntry(boardId, boardEntry);
    }

    @GetMapping("/{boardId}")
    public List<BoardEntry> getBoardEntries(@PathVariable("boardId") UUID boardId) {
        return boardEntryService.getBoardEntries(boardId);
    }


}
