package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.constants.BoardUserRole;
import it.vitalegi.cosucce.budget.dto.AddBoardDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardDto;
import it.vitalegi.cosucce.budget.entity.BoardEntity;
import it.vitalegi.cosucce.budget.entity.BoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.repository.BoardAccountRepository;
import it.vitalegi.cosucce.budget.repository.BoardCategoryRepository;
import it.vitalegi.cosucce.budget.repository.BoardEntryRepository;
import it.vitalegi.cosucce.budget.repository.BoardRepository;
import it.vitalegi.cosucce.budget.repository.BoardUserRepository;
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
    BoardAccountRepository boardAccountRepository;
    BoardCategoryRepository boardCategoryRepository;
    BoardEntryRepository boardEntryRepository;

    BoardMapper boardMapper;
    OptimisticLockService optimisticLockService;

    @Transactional
    public Board addBoard(AddBoardDto dto, UUID userId) {
        if (dto.getBoardId() != null) {
            if (boardRepository.findById(dto.getBoardId()).isPresent()) {
                throw new IllegalArgumentException("Invalid ID");
            }
        }

        var entity = new BoardEntity();
        var ts = Instant.now();
        entity.setBoardId(dto.getBoardId());
        entity.setName(dto.getName());
        entity.setEtag(dto.getEtag());
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

    @Transactional
    public Board updateBoard(UUID boardId, UpdateBoardDto dto) {
        var entity = getBoard(boardId);
        optimisticLockService.checkLock(boardId, entity.getEtag(), dto.getEtag());
        entity.setEtag(dto.getNewETag());
        entity.setName(dto.getName());
        entity.setLastUpdate(Instant.now());
        entity = boardRepository.save(entity);
        return boardMapper.toBoard(entity);
    }

    @Transactional
    public void deleteBoard(UUID id) {
        var entity = boardRepository.findById(id);
        if (entity.isEmpty()) {
            return;
        }
        boardRepository.deleteById(id);
    }

    public List<Board> getVisibleBoards(UUID userId) {
        return boardRepository.findAllByUser(userId).stream().map(boardMapper::toBoard).toList();
    }

    protected BoardEntity getBoard(UUID boardId) {
        var entity = boardRepository.findById(boardId);
        if (entity.isEmpty()) {
            throw new BudgetException("Board " + boardId + " not found");
        }
        return entity.get();
    }
}
