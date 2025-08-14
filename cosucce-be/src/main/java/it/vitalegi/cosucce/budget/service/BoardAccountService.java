package it.vitalegi.cosucce.budget.service;

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

    public List<BoardAccount> getBoardAccounts(UUID boardId) {
        return boardAccountRepository.findAllByBoardId(boardId).stream().map(boardMapper::toAccount).toList();
    }

    public BoardAccount addBoardAccount(UUID boardId, String label, String icon) {
        var entity = new BoardAccountEntity();
        var ts = Instant.now();
        entity.setBoardId(boardId);
        entity.setLabel(label);
        entity.setIcon(icon);
        entity.setEnabled(true);
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
    public BoardAccount updateBoardAccount(UUID boardId, UUID accountId, String label, String icon, boolean enabled) {
        var opt = boardAccountRepository.findById(accountId);
        if (opt.isEmpty()) {
            throw new BudgetException("Account " + accountId + " not found");
        }
        var entity = opt.get();
        if (!entity.getBoardId().equals(boardId)) {
            throw new BudgetException("Account " + accountId + " is not part of board " + boardId);
        }
        entity.setLabel(label);
        entity.setIcon(icon);
        entity.setEnabled(enabled);
        entity.setLastUpdate(Instant.now());
        entity = boardAccountRepository.save(entity);
        return boardMapper.toAccount(entity);
    }
}
