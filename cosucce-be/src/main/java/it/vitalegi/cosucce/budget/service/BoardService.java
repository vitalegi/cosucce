package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constants.BoardUserRole;
import it.vitalegi.cosucce.budget.entity.BoardEntity;
import it.vitalegi.cosucce.budget.entity.BoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.repository.BoardRepository;
import it.vitalegi.cosucce.budget.repository.BoardUserRepository;
import it.vitalegi.cosucce.iam.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class BoardService {

    BoardRepository boardRepository;
    BoardUserRepository boardUserRepository;
    UserRepository userRepository;

    BoardMapper boardMapper;

    @Transactional
    public Board createBoard(UUID userId) {
        var entity = new BoardEntity();
        var ts = Instant.now();
        entity.setName(UUID.randomUUID().toString());
        entity.setCreationDate(ts);
        entity.setLastUpdate(ts);
        entity = boardRepository.save(entity);

        var buEntity = new BoardUserEntity();
        buEntity.setId(new BoardUserId(entity.getBoardId(), userId));
        buEntity.setCreationDate(ts);
        buEntity.setLastUpdate(ts);
        buEntity.setRole(BoardUserRole.OWNER.name());
        boardUserRepository.save(buEntity);

        return boardMapper.toBoard(entity);
    }

    public List<Board> getVisibleBoards(UUID userId) {
        return boardRepository.findAllByUser(userId).stream().map(boardMapper::toBoard).toList();
    }


}
