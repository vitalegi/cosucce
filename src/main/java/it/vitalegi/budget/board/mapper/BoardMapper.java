package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.util.ObjectUtil;
import org.springframework.stereotype.Service;

@Service
public class BoardMapper {
    public Board map(BoardEntity source) {
        Board out = ObjectUtil.copy(source, new Board());
        out.setOwnerId(source.getOwner().getId());
        return out;
    }

    public BoardEntity map(Board source) {
        BoardEntity out = ObjectUtil.copy(source, new BoardEntity());
        return out;
    }
}
