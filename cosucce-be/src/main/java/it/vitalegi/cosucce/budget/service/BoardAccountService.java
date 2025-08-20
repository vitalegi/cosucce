package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.dto.AddBoardAccountDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardAccountDto;
import it.vitalegi.cosucce.budget.entity.BoardAccountEntity;
import it.vitalegi.cosucce.budget.exception.BudgetException;
import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.BoardAccount;
import it.vitalegi.cosucce.budget.repository.BoardAccountRepository;
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
public class BoardAccountService {
    BoardAccountRepository boardAccountRepository;

    BoardMapper boardMapper;
    OptimisticLockService optimisticLockService;

    public List<BoardAccount> getBoardAccounts(UUID boardId) {
        return boardAccountRepository.findAllByBoardId(boardId).stream().map(boardMapper::toAccount).toList();
    }

    public BoardAccount addBoardAccount(UUID boardId, AddBoardAccountDto dto) {
        if (dto.getAccountId() != null) {
            if (boardAccountRepository.findById(dto.getAccountId()).isPresent()) {
                throw new IllegalArgumentException("Invalid ID");
            }
        }
        var entity = new BoardAccountEntity();
        var ts = Instant.now();
        entity.setAccountId(dto.getAccountId());
        entity.setBoardId(boardId);
        entity.setEtag(dto.getEtag());
        entity.setLabel(dto.getLabel());
        entity.setIcon(dto.getIcon());
        entity.setEnabled(dto.isEnabled());
        entity.setCreationDate(ts);
        entity.setLastUpdate(ts);
        try {
            entity = boardAccountRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new BudgetException("Board " + boardId + " not found", e);
        }
        return boardMapper.toAccount(entity);
    }

    @Transactional
    public BoardAccount updateBoardAccount(UUID boardId,  UUID accountId, UpdateBoardAccountDto dto) {
        var opt = boardAccountRepository.findById(accountId);
        if (opt.isEmpty()) {
            throw new BudgetException("Account " + accountId + " not found");
        }
        var entity = opt.get();
        optimisticLockService.checkLock(accountId, entity.getEtag(), dto.getEtag());
        if (!entity.getBoardId().equals(boardId)) {
            throw new BudgetException("Account " + accountId + " is not part of board " + boardId);
        }
        entity.setEtag(dto.getNewETag());
        entity.setLabel(dto.getLabel());
        entity.setIcon(dto.getIcon());
        entity.setEnabled(dto.isEnabled());
        entity.setLastUpdate(Instant.now());
        entity = boardAccountRepository.save(entity);
        return boardMapper.toAccount(entity);
    }

    @Transactional
    public void deleteBoardAccount(UUID boardId, UUID id) {
        var entity = boardAccountRepository.findById(id);
        if (entity.isEmpty()) {
            return;
        }
        var e = entity.get();
        if (!e.getBoardId().equals(boardId)) {
            throw new BudgetException("Account " + id + " is not part of board " + boardId);
        }
        boardAccountRepository.deleteById(id);
    }

    protected BoardAccountEntity getBoardAccount(UUID id) {
        var entity = boardAccountRepository.findById(id);
        if (entity.isEmpty()) {
            throw new BudgetException("Account " + id + " not found");
        }
        return entity.get();
    }
}
