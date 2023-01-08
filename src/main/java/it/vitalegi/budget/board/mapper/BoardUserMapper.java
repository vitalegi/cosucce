package it.vitalegi.budget.board.mapper;

import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardUserMapper {

    @Autowired
    UserMapper userMapper;

    public List<BoardUser> map(List<BoardUserEntity> entities) {
        return entities.stream()//
                .collect(Collectors.groupingBy(b -> b.getUser().getId()))//
                .values().stream() //
                .map(this::mapUser) //
                .collect(Collectors.toList());
    }

    public BoardUser mapUser(BoardUserEntity entity) {
        BoardUser dto = new BoardUser();
        dto.setUser(userMapper.map(entity.getUser()));
        dto.setRoles(new ArrayList<>());
        dto.getRoles().add(entity.getRole());
        return dto;
    }

    private BoardUser mapUser(List<BoardUserEntity> entities) {
        if (entities.isEmpty()) {
            throw new IllegalArgumentException("List must have at least one element");
        }
        long count = entities.stream().map(b -> b.getUser().getId()).distinct().count();
        if (count != 1) {
            throw new IllegalArgumentException("All entries must refer the same user, found " + count + " different users");
        }
        BoardUser dto = new BoardUser();
        dto.setUser(userMapper.map(entities.get(0).getUser()));
        dto.setRoles(entities.stream().map(BoardUserEntity::getRole).collect(Collectors.toList()));
        return dto;
    }

}
