package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.dto.AddBoardEntryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardEntryDto;
import it.vitalegi.cosucce.budget.entity.BoardEntryEntity;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.BoardEntry;
import it.vitalegi.cosucce.budget.repository.BoardAccountRepository;
import it.vitalegi.cosucce.budget.repository.BoardCategoryRepository;
import it.vitalegi.cosucce.budget.repository.BoardEntryRepository;
import it.vitalegi.cosucce.budget.repository.BoardRepository;
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
public class BoardEntryService {
    BoardEntryRepository boardEntryRepository;
    BoardRepository boardRepository;
    BoardAccountRepository boardAccountRepository;
    BoardCategoryRepository boardCategoryRepository;

    BoardMapper boardMapper;
    OptimisticLockService optimisticLockService;

    public List<BoardEntry> getBoardEntries(UUID boardId) {
        return boardEntryRepository.findAllByBoardId(boardId).stream().map(boardMapper::toEntry).toList();
    }

    @Transactional
    public BoardEntry addBoardEntry(UUID boardId, AddBoardEntryDto request, UUID userId) {
        if (request.getEntryId() != null) {
            if (boardEntryRepository.findById(request.getEntryId()).isPresent()) {
                throw new IllegalArgumentException("Invalid ID");
            }
        }

        validateBoard(boardId);
        validateAccount(boardId, request.getAccountId());
        validateCategory(boardId, request.getCategoryId());

        var entity = new BoardEntryEntity();
        var ts = Instant.now();
        entity.setEntryId(request.getEntryId());
        entity.setBoardId(boardId);
        entity.setDate(request.getDate());
        entity.setAccountId(request.getAccountId());
        entity.setCategoryId(request.getCategoryId());
        entity.setDescription(request.getDescription());
        entity.setAmount(request.getAmount());
        entity.setLastUpdatedBy(userId);
        entity.setEtag(request.getEtag());
        entity.setCreationDate(ts);
        entity.setLastUpdate(ts);
        try {
            entity = boardEntryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new BudgetException("Error creating entry", e);
        }
        return boardMapper.toEntry(entity);
    }

    @Transactional
    public BoardEntry updateBoardEntry(UUID boardId, UUID entryId, UpdateBoardEntryDto request, UUID userId) {
        var opt = boardEntryRepository.findById(entryId);
        if (opt.isEmpty()) {
            throw new BudgetException("Entry " + entryId + " not found");
        }
        var entity = opt.get();
        optimisticLockService.checkLock(entryId, entity.getEtag(), request.getEtag());
        if (!entity.getBoardId().equals(boardId)) {
            throw new BudgetException("Entry " + entryId + " is not part of board " + boardId);
        }

        validateAccount(boardId, request.getAccountId());
        validateCategory(boardId, request.getCategoryId());

        entity.setBoardId(boardId);
        entity.setDate(request.getDate());
        entity.setEtag(request.getNewETag());
        entity.setAccountId(request.getAccountId());
        entity.setCategoryId(request.getCategoryId());
        entity.setDescription(request.getDescription());
        entity.setAmount(request.getAmount());

        entity.setLastUpdatedBy(userId);
        entity.setLastUpdate(Instant.now());
        entity = boardEntryRepository.save(entity);
        return boardMapper.toEntry(entity);
    }

    @Transactional
    public void deleteBoardEntry(UUID boardId, UUID id) {
        var entity = boardEntryRepository.findById(id);
        if (entity.isEmpty()) {
            return;
        }
        var e = entity.get();
        if (!e.getBoardId().equals(boardId)) {
            throw new BudgetException("Entry " + id + " is not part of board " + boardId);
        }
        boardEntryRepository.deleteById(id);
    }

    protected void validateBoard(UUID boardId) {
        var entity = boardRepository.findById(boardId);
        if (entity.isEmpty()) {
            throw new BudgetException("Board " + boardId + " not found");
        }
    }

    protected void validateAccount(UUID boardId, UUID accountId) {
        var entity = boardAccountRepository.findById(accountId);
        if (entity.isEmpty()) {
            throw new BudgetException("Account " + accountId + " not found");
        }
        if (!entity.get().getBoardId().equals(boardId)) {
            throw new BudgetException("Account " + accountId + " is not part of board " + boardId);
        }
    }

    protected void validateCategory(UUID boardId, UUID categoryId) {
        var entity = boardCategoryRepository.findById(categoryId);
        if (entity.isEmpty()) {
            throw new BudgetException("Category " + categoryId + " not found");
        }
        if (!entity.get().getBoardId().equals(boardId)) {
            throw new BudgetException("Category " + categoryId + " is not part of board " + boardId);
        }
    }

    protected BoardEntryEntity getBoardEntry(UUID id) {
        var entity = boardEntryRepository.findById(id);
        if (entity.isEmpty()) {
            throw new BudgetException("Entry " + id + " not found");
        }
        return entity.get();
    }
}
