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
import it.vitalegi.budget.user.UserService;
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
    UserService userService;
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
        UserEntity currentUser = userService.getCurrentUserEntity();
        if (user.getId() == currentUser.getId()) {
            throw new IllegalArgumentException("Cannot change self role");
        }
        List<BoardUserEntity> rolesOfUser = entries.stream().filter(e -> e.getUser().equals(user)) //
                .collect(Collectors.toList());
        if (rolesOfUser.isEmpty()) {
            BoardUserEntity entity = new BoardUserEntity();
            entity.setBoard(board);
            entity.setUser(user);
            entity.setRole(role.name());
            return boardUserRepository.save(entity);
        } else {
            BoardUserEntity entity = rolesOfUser.get(0);
            entity.setRole(role.name());
            return boardUserRepository.save(entity);
        }
    }

    public BoardUser addBoardUser(BoardEntity board, UserEntity user, BoardUserRole role) {
        BoardUserEntity entry = addBoardUserEntity(board, user, role);
        return boardUserMapper.map(entry);
    }
}
