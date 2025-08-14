package it.vitalegi.cosucce.budget.service;

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

import java.math.BigDecimal;
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

    public List<BoardEntry> getBoardEntries(UUID boardId) {
        return boardEntryRepository.findAllByBoardId(boardId).stream().map(boardMapper::toEntry).toList();
    }

    @Transactional
    public BoardEntry addBoardEntry( //
                                     UUID boardId, UUID accountId, UUID categoryId, String description, BigDecimal amount, UUID lastUpdatedBy) {

        validateBoard(boardId);
        validateAccount(boardId, accountId);
        validateCategory(boardId, categoryId);

        var entity = new BoardEntryEntity();
        var ts = Instant.now();
        entity.setBoardId(boardId);
        entity.setAccountId(accountId);
        entity.setCategoryId(categoryId);
        entity.setDescription(description);
        entity.setAmount(amount);
        entity.setLastUpdatedBy(lastUpdatedBy);
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
    public BoardEntry updateBoardEntry(UUID boardId, UUID entryId, UUID accountId, UUID categoryId, String description, BigDecimal amount, UUID lastUpdatedBy) {
        var opt = boardEntryRepository.findById(entryId);
        if (opt.isEmpty()) {
            throw new BudgetException("Entry " + entryId + " not found");
        }
        var entity = opt.get();
        if (!entity.getBoardId().equals(boardId)) {
            throw new BudgetException("Entry " + entryId + " is not part of board " + boardId);
        }

        validateAccount(boardId, accountId);
        validateCategory(boardId, categoryId);

        entity.setBoardId(boardId);
        entity.setAccountId(accountId);
        entity.setCategoryId(categoryId);
        entity.setDescription(description);
        entity.setAmount(amount);
        entity.setLastUpdatedBy(lastUpdatedBy);

        entity.setLastUpdate(Instant.now());
        entity = boardEntryRepository.save(entity);
        return boardMapper.toEntry(entity);
    }

    @Transactional
    public BoardEntry deleteBoardEntry(UUID boardId, UUID entryId) {
        var opt = boardEntryRepository.findById(entryId);
        if (opt.isEmpty()) {
            throw new BudgetException("Entry " + entryId + " not found");
        }
        var entity = opt.get();
        if (!entity.getBoardId().equals(boardId)) {
            throw new BudgetException("Entry " + entryId + " is not part of board " + boardId);
        }
        boardEntryRepository.delete(entity);
        return boardMapper.toEntry(entity);
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
}
