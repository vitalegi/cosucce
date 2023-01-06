package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.mapper.BoardEntryMapper;
import it.vitalegi.budget.board.repository.BoardEntryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Service
public class BoardEntryService {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    BoardEntryRepository repository;
    @Autowired
    BoardEntryMapper mapper;

    @Autowired
    BoardPermissionService boardPermissionService;

    public BoardEntry addBoardEntry(UUID boardId, BoardEntry boardEntry) {
        boardPermissionService.checkGrant(boardId, BoardGrant.EDIT);
        BoardEntryEntity entry = new BoardEntryEntity();
        entry.setId(UUID.randomUUID());
        entry.setBoardId(boardId);
        entry.setDate(boardEntry.getDate());
        LocalDateTime now = LocalDateTime.now();
        entry.setCreationDate(now);
        entry.setLastUpdate(now);
        entry.setOwnerId(authenticationService.getId());
        entry.setCategory(boardEntry.getCategory());
        entry.setDescription(boardEntry.getDescription());
        entry.setAmount(boardEntry.getAmount());
        return mapper.map(repository.save(entry));
    }

    public List<BoardEntry> getBoardEntries(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.VIEW);
        List<BoardEntryEntity> entries = repository.findByBoardId(boardId);
        log.info("entries: {}/{}. {}", entries.size(), entries);
        return StreamSupport.stream(entries.spliterator(), false).map(mapper::map).collect(Collectors.toList());
    }
}
