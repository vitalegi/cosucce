package it.vitalegi.cosucce.budget.mapper;

import it.vitalegi.cosucce.budget.entity.BoardAccountEntity;
import it.vitalegi.cosucce.budget.entity.BoardCategoryEntity;
import it.vitalegi.cosucce.budget.entity.BoardEntity;
import it.vitalegi.cosucce.budget.entity.BoardEntryEntity;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.model.BoardAccount;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.model.BoardEntry;
import org.springframework.stereotype.Service;

@Service
public class BoardMapper {

    public Board toBoard(BoardEntity entity) {
        var out = new Board();
        out.setBoardId(entity.getBoardId());
        out.setName(entity.getName());
        out.setEtag(entity.getEtag());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }

    public BoardAccount toAccount(BoardAccountEntity entity) {
        var out = new BoardAccount();
        out.setAccountId(entity.getAccountId());
        out.setBoardId(entity.getBoardId());
        out.setEtag(entity.getEtag());
        out.setLabel(entity.getLabel());
        out.setIcon(entity.getIcon());
        out.setEnabled(entity.isEnabled());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }

    public BoardCategory toCategory(BoardCategoryEntity entity) {
        var out = new BoardCategory();
        out.setCategoryId(entity.getCategoryId());
        out.setBoardId(entity.getBoardId());
        out.setEtag(entity.getEtag());
        out.setLabel(entity.getLabel());
        out.setIcon(entity.getIcon());
        out.setEnabled(entity.isEnabled());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }

    public BoardEntry toEntry(BoardEntryEntity entity) {
        var out = new BoardEntry();
        out.setEntryId(entity.getEntryId());
        out.setBoardId(entity.getBoardId());
        out.setDate(entity.getDate());
        out.setEtag(entity.getEtag());
        out.setAccountId(entity.getAccountId());
        out.setCategoryId(entity.getCategoryId());
        out.setDescription(entity.getDescription());
        out.setAmount(entity.getAmount());
        out.setLastUpdatedBy(entity.getLastUpdatedBy());
        out.setCreationDate(entity.getCreationDate());
        out.setLastUpdate(entity.getLastUpdate());
        return out;
    }
}
