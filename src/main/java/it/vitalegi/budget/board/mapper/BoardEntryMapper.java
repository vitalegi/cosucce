package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.util.ObjectUtil;
import org.springframework.stereotype.Service;

@Service
public class BoardEntryMapper {
    public BoardEntry map(BoardEntryEntity source) {
        BoardEntry dto = new BoardEntry();
        dto.setId(source.getId());
        dto.setBoardId(source.getBoard().getId());
        dto.setDate(source.getDate());
        dto.setCategory(source.getCategory());
        dto.setAmount(source.getAmount());
        dto.setDescription(source.getDescription());
        dto.setOwnerId(source.getOwner().getId());
        dto.setCreationDate(source.getCreationDate());
        dto.setLastUpdate(source.getLastUpdate());
        return dto;
    }

    public BoardEntryEntity map(BoardEntry source) {
        BoardEntryEntity out = ObjectUtil.copy(source, new BoardEntryEntity());
        return out;
    }
}
