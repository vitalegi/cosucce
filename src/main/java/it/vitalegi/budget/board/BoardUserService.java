package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.board.mapper.BoardUserMapper;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.entity.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardUserService {
    @Autowired
    BoardUserRepository boardUserRepository;
    @Autowired
    BoardPermissionService boardPermissionService;

    @Autowired
    BoardUserMapper boardUserMapper;

    public List<BoardUser> getBoardUsers(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.VIEW);
        List<BoardUserEntity> boardUsers = boardUserRepository.findByBoard_Id(boardId);
        return boardUserMapper.map(boardUsers);
    }

    @Transactional
    public BoardUserEntity addBoardUserEntity(BoardEntity board, UserEntity user, BoardUserRole role) {
        boardPermissionService.checkGrant(board.getId(), BoardGrant.EDIT_BOARD);
        List<BoardUserEntity> entries = boardUserRepository.findByBoard_Id(board.getId());

        List<BoardUserEntity> existingEntries = entries.stream().filter(e -> e.getUser().equals(user)) //
                .filter(e -> e.getRole().equals(role.name())) //
                .collect(Collectors.toList());
        if (!existingEntries.isEmpty()) {
            log.info("Record already present {}", existingEntries);
        }

        BoardUserEntity entity = new BoardUserEntity();
        entity.setBoard(board);
        entity.setUser(user);
        entity.setRole(role.name());
        return boardUserRepository.save(entity);
    }

    public BoardUser addBoardUser(BoardEntity board, UserEntity user, BoardUserRole role) {
        return boardUserMapper.mapUser(addBoardUserEntity(board, user, role));
    }
}
