package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardUserMapper {

    @Autowired
    UserMapper userMapper;

    public List<BoardUser> map(List<BoardUserEntity> entities) {
        return entities.stream()//
                .map(this::map)
                .collect(Collectors.toList());
    }

    public BoardUser map(BoardUserEntity entity) {
        BoardUser dto = new BoardUser();
        dto.setUser(userMapper.map(entity.getUser()));
        dto.setRole(BoardUserRole.valueOf(entity.getRole()));
        return dto;
    }
}
