package it.vitalegi.budget.resource;

import it.vitalegi.budget.board.BoardService;
import it.vitalegi.budget.board.dto.Board;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/board")
public class BoardResource {

    @Autowired
    BoardService service;

    @PutMapping()
    public Board addBoard(@RequestBody Board board) {
        return service.add(board.getName());
    }

    @GetMapping()
    public List<Board> getBoards() {
        return service.getVisibleBoards();
    }

}
