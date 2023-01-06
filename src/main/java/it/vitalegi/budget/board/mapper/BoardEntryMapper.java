package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.util.ObjectUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardEntryMapper {
    public BoardEntry map(BoardEntryEntity source) {

        BoardEntry out = ObjectUtil.copy(source, new BoardEntry());
        return out;
    }

    public BoardEntryEntity map(BoardEntry source) {
        BoardEntryEntity out = ObjectUtil.copy(source, new BoardEntryEntity());
        return out;
    }
}
