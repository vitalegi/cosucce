package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardEntryRepository;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.entity.UserEntity;
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
public class BoardService {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardEntryRepository boardEntryRepository;

    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    UserService userService;

    @Autowired
    BoardMapper mapper;

    @Autowired
    BoardPermissionService boardPermissionService;

    @Transactional
    public Board addBoard(String name) {
        UserEntity owner = userService.getCurrentUserEntity();
        BoardEntity board = new BoardEntity();
        board.setName(name);
        LocalDateTime now = LocalDateTime.now();
        board.setCreationDate(now);
        board.setLastUpdate(now);
        BoardEntity out = boardRepository.save(board);
        log.info("Board is created. Board={}, User={}", out.getId(), owner.getId());

        BoardUserEntity entity = new BoardUserEntity();
        entity.setBoard(board);
        entity.setUser(owner);
        entity.setRole(BoardUserRole.OWNER.name());
        boardUserRepository.save(entity);
        log.info("User is owner of board. Board={}, User={}", out.getId(), owner.getId());
        return mapper.map(out);
    }

    public Board getBoard(UUID id) {
        BoardEntity board = getBoardEntity(id);
        return mapper.map(board);
    }

    public BoardEntity getBoardEntity(UUID id) {
        boardPermissionService.checkGrant(id, BoardGrant.BOARD_VIEW);
        return boardRepository.findById(id).get();
    }

    public List<Board> getVisibleBoards() {
        Iterable<BoardEntity> boards = boardRepository.findVisibleBoards(userService.getCurrentUser().getId());
        return StreamSupport.stream(boards.spliterator(), false) //
                .map(board -> mapper.map(board)) //
                .collect(Collectors.toList());
    }

    public BoardEntry addBoardEntry(UUID boardId, BoardEntry boardEntry) {
        UserEntity user = userService.getCurrentUserEntity();
        boardPermissionService.checkGrant(user, boardId, BoardGrant.BOARD_ENTRY_EDIT);
        log.info("Current user can edit board entries");

        UserEntity author = userService.getUserEntity(boardEntry.getOwnerId());
        boardPermissionService.checkGrant(author, boardId, BoardGrant.BOARD_ENTRY_EDIT);
        log.info("Author {} can edit board entries", author.getId());

        BoardEntity board = boardRepository.findById(boardId).get();

        BoardEntryEntity entry = new BoardEntryEntity();
        entry.setBoard(board);
        entry.setDate(boardEntry.getDate());
        LocalDateTime now = LocalDateTime.now();
        entry.setCreationDate(now);
        entry.setLastUpdate(now);
        entry.setOwner(author);
        entry.setCategory(boardEntry.getCategory());
        entry.setDescription(boardEntry.getDescription());
        entry.setAmount(boardEntry.getAmount());
        BoardEntryEntity newEntry = boardEntryRepository.save(entry);
        log.info("Created boardEntry. board={}, ownerId={}, entryId={}", boardId, author.getId(), newEntry.getId());
        return mapper.map(newEntry);
    }

    @Transactional
    public BoardEntry updateBoardEntry(UUID boardId, BoardEntry boardEntry) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_ENTRY_EDIT);

        BoardEntryEntity entry = boardEntryRepository.findById(boardId).get();
        entry.setDate(boardEntry.getDate());
        LocalDateTime now = LocalDateTime.now();
        entry.setLastUpdate(now);
        entry.setCategory(boardEntry.getCategory());
        entry.setDescription(boardEntry.getDescription());
        entry.setAmount(boardEntry.getAmount());
        BoardEntryEntity newEntry = boardEntryRepository.save(entry);
        log.info("Updated boardEntry. board={}, ownerId={}, entryId={}", boardId, boardEntry.getOwnerId(), newEntry.getId());
        return mapper.map(newEntry);
    }


    public List<BoardEntry> getBoardEntries(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<BoardEntryEntity> entries = boardEntryRepository.findByBoardId(boardId);
        return StreamSupport.stream(entries.spliterator(), false).map(mapper::map).collect(Collectors.toList());
    }

    public List<String> getCategories(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<String> categories = boardEntryRepository.findCategories(boardId);
        categories.sort((a, b) -> a.compareTo(b));
        return categories;
    }

    public List<BoardUser> getBoardUsers(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<BoardUserEntity> boardUsers = boardUserRepository.findByBoard_Id(boardId);
        return mapper.map(boardUsers);
    }


    public BoardUser addBoardUser(BoardEntity board, UserEntity user, BoardUserRole role) {
        BoardUserEntity entry = addBoardUserEntity(board, user, role);
        return mapper.map(entry);
    }

    @Transactional
    public BoardUserEntity addBoardUserEntity(BoardEntity board, UserEntity user, BoardUserRole role) {
        boardPermissionService.checkGrant(board.getId(), BoardGrant.BOARD_USER_ROLE_EDIT);
        UserEntity currentUser = userService.getCurrentUserEntity();
        if (user.getId() == currentUser.getId()) {
            throw new IllegalArgumentException("Cannot change self role");
        }
        List<BoardUserEntity> entries = boardUserRepository.findByBoard_Id(board.getId());
        List<BoardUserEntity> rolesOfUser = entries.stream().filter(e -> e.getUser().equals(user)) //
                .collect(Collectors.toList());
        if (rolesOfUser.isEmpty()) {
            log.info("ADD_BOARD_USER board={}, user={}, owner={}, role={}", board.getId(), user.getId(), currentUser.getId(), role);
            BoardUserEntity entity = new BoardUserEntity();
            entity.setBoard(board);
            entity.setUser(user);
            entity.setRole(role.name());
            return boardUserRepository.save(entity);
        } else {
            log.info("UPDATE_BOARD_USER board={}, user={}, owner={}, role={}", board.getId(), user.getId(), currentUser.getId(), role);
            BoardUserEntity entity = rolesOfUser.get(0);
            entity.setRole(role.name());
            return boardUserRepository.save(entity);
        }
    }
}
