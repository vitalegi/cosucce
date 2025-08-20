package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.dto.AddBoardCategoryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardCategoryDto;
import it.vitalegi.cosucce.budget.entity.BoardCategoryEntity;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.repository.BoardCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class BoardCategoryService {
    BoardCategoryRepository boardCategoryRepository;

    BoardMapper boardMapper;
    OptimisticLockService optimisticLockService;

    public List<BoardCategory> getBoardCategories(UUID boardId) {
        return boardCategoryRepository.findAllByBoardId(boardId).stream().map(boardMapper::toCategory).toList();
    }

    public BoardCategory addBoardCategory(UUID boardId, AddBoardCategoryDto dto) {
        if (dto.getCategoryId() != null) {
            if (boardCategoryRepository.findById(dto.getCategoryId()).isPresent()) {
                throw new IllegalArgumentException("Invalid ID");
            }
        }

        var entity = new BoardCategoryEntity();
        var ts = Instant.now();
        entity.setCategoryId(dto.getCategoryId());
        entity.setBoardId(boardId);
        entity.setEtag(dto.getEtag());
        entity.setLabel(dto.getLabel());
        entity.setIcon(dto.getIcon());
        entity.setEnabled(dto.isEnabled());
        entity.setCreationDate(ts);
        entity.setLastUpdate(ts);
        try {
            entity = boardCategoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new BudgetException("Board " + boardId + " not found", e);
        }
        return boardMapper.toCategory(entity);
    }

    @Transactional
    public BoardCategory updateBoardCategory(UUID boardId, UUID categoryId, UpdateBoardCategoryDto dto) {
        var opt = boardCategoryRepository.findById(categoryId);
        if (opt.isEmpty()) {
            throw new BudgetException("Category " + categoryId + " not found");
        }
        var entity = opt.get();
        optimisticLockService.checkLock(categoryId, entity.getEtag(), dto.getEtag());
        if (!entity.getBoardId().equals(boardId)) {
            throw new BudgetException("Category " + categoryId + " is not part of board " + boardId);
        }
        entity.setEtag(dto.getNewETag());
        entity.setLabel(dto.getLabel());
        entity.setIcon(dto.getIcon());
        entity.setEnabled(dto.isEnabled());
        entity.setLastUpdate(Instant.now());
        entity = boardCategoryRepository.save(entity);
        return boardMapper.toCategory(entity);
    }

    @Transactional
    public void deleteBoardCategory(UUID boardId, UUID id) {
        var entity = boardCategoryRepository.findById(id);
        if (entity.isEmpty()) {
            return;
        }
        var e = entity.get();
        if (!e.getBoardId().equals(boardId)) {
            throw new BudgetException("Category " + id + " is not part of board " + boardId);
        }
        boardCategoryRepository.deleteById(id);
    }

    protected BoardCategoryEntity getBoardCategory(UUID id) {
        var entity = boardCategoryRepository.findById(id);
        if (entity.isEmpty()) {
            throw new BudgetException("Category " + id + " not found");
        }
        return entity.get();
    }
}
