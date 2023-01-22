package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.entity.BoardSplitEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.user.mapper.UserMapper;
import it.vitalegi.budget.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardMapper {

    @Autowired
    UserMapper userMapper;

    public Board map(BoardEntity source) {
        Board out = ObjectUtil.copy(source, new Board());
        return out;
    }

    public BoardEntity map(Board source) {
        BoardEntity out = ObjectUtil.copy(source, new BoardEntity());
        return out;
    }

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

    public List<BoardUser> map(List<BoardUserEntity> entities) {
        return entities.stream()//
                .map(this::map).collect(Collectors.toList());
    }

    public BoardUser map(BoardUserEntity entity) {
        BoardUser dto = new BoardUser();
        dto.setUser(userMapper.map(entity.getUser()));
        dto.setRole(BoardUserRole.valueOf(entity.getRole()));
        return dto;
    }

    public BoardSplit map(BoardSplitEntity entity) {
        BoardSplit dto = ObjectUtil.copy(entity, new BoardSplit());
        dto.setBoardId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        return dto;
    }
}
