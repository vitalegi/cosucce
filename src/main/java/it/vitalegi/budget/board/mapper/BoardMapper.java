package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import org.springframework.beans.BeanUtils;

public class BoardMapper {
    public static Board entity2dto(BoardEntity entity) {
        Board dto = new Board();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
