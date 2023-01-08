package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.mapper.BoardEntryMapper;
import it.vitalegi.budget.board.repository.BoardEntryRepository;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardEntryService {

    @Autowired
    UserService userService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardEntryRepository repository;
    @Autowired
    BoardEntryMapper mapper;

    @Autowired
    BoardPermissionService boardPermissionService;

    public BoardEntry addBoardEntry(UUID boardId, BoardEntry boardEntry) {
        boardPermissionService.checkGrant(boardId, BoardGrant.EDIT_BOARD_ENTRY);
        BoardEntryEntity entry = new BoardEntryEntity();
        entry.setBoard(boardRepository.findById(boardId).get());
        entry.setDate(boardEntry.getDate());
        LocalDateTime now = LocalDateTime.now();
        entry.setCreationDate(now);
        entry.setLastUpdate(now);
        entry.setOwner(userService.getCurrentUserEntity());
        entry.setCategory(boardEntry.getCategory());
        entry.setDescription(boardEntry.getDescription());
        entry.setAmount(boardEntry.getAmount());
        return mapper.map(repository.save(entry));
    }

    @Transactional
    public BoardEntry updateBoardEntry(UUID boardId, BoardEntry boardEntry) {
        boardPermissionService.checkGrant(boardId, BoardGrant.EDIT_BOARD_ENTRY);

        BoardEntryEntity entry = repository.findById(boardId).get();
        entry.setDate(boardEntry.getDate());
        LocalDateTime now = LocalDateTime.now();
        entry.setLastUpdate(now);
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
