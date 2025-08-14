package it.vitalegi.cosucce.budget.mapper;

import it.vitalegi.cosucce.budget.entity.BoardEntity;
import it.vitalegi.cosucce.budget.model.Board;
import org.springframework.stereotype.Service;

@Service
public class BoardMapper {

    public Board toBoard(BoardEntity entity) {
        var out = new Board();
        out.setBoardId(entity.getBoardId());
        out.setName(entity.getName());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }
}
