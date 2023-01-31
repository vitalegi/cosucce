package it.vitalegi.budget.board;

import it.vitalegi.budget.auth.BoardGrant;
import it.vitalegi.budget.board.analysis.AggregatedAnalysisService;
import it.vitalegi.budget.board.analysis.dto.MonthlyUserAnalysis;
import it.vitalegi.budget.board.constant.BoardUserRole;
import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.dto.BoardEntry;
import it.vitalegi.budget.board.dto.BoardInvite;
import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.BoardUser;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.entity.BoardInviteEntity;
import it.vitalegi.budget.board.entity.BoardSplitEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import it.vitalegi.budget.board.repository.BoardEntryRepository;
import it.vitalegi.budget.board.repository.BoardInviteRepository;
import it.vitalegi.budget.board.repository.BoardRepository;
import it.vitalegi.budget.board.repository.BoardSplitRepository;
import it.vitalegi.budget.board.repository.BoardUserRepository;
import it.vitalegi.budget.board.repository.util.BoardEntryGroupByMonthUserCategory;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.UserService;
import it.vitalegi.budget.user.entity.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    BoardSplitRepository boardSplitRepository;

    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    UserService userService;

    @Autowired
    BoardMapper mapper;

    @Autowired
    BoardPermissionService boardPermissionService;

    @Autowired
    AggregatedAnalysisService aggregatedAnalysisService;

    @Autowired
    BoardInviteRepository boardInviteRepository;

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

    public BoardInvite addBoardInvite(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_MANAGE_MEMBER);

        UserEntity currentUser = userService.getCurrentUserEntity();
        BoardEntity board = getBoardEntity(boardId);

        BoardInviteEntity entity = new BoardInviteEntity();
        entity.setOwner(currentUser);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreationDate(now);
        entity.setLastUpdate(now);
        entity.setBoard(board);
        BoardInviteEntity out = boardInviteRepository.save(entity);
        log.info("ADD_BOARD_INVITE board={}, user={}, invite={}", board.getId(), currentUser.getId(), out.getId());
        return mapper.map(out);
    }

    @Transactional
    public BoardSplit addBoardSplit(UUID boardId, long userId, Integer fromYear, Integer fromMonth, Integer toYear,
                                    Integer toMonth, BigDecimal value) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_EDIT);
        BoardEntity boardEntity = getBoardEntity(boardId);
        log.info("User can work on this board");
        BoardUserEntity user = boardUserRepository.findUserBoard(boardId, userId);
        if (user == null) {
            throw new IllegalArgumentException("User " + userId + " is not part of the board");
        }
        log.info("Requested user is part of the board");

        UserEntity userEntity = userService.getUserEntity(userId);
        BoardSplitEntity boardSplitEntity = new BoardSplitEntity();
        boardSplitEntity.setUser(userEntity);
        boardSplitEntity.setBoard(boardEntity);
        boardSplitEntity.setFromMonth(fromMonth);
        boardSplitEntity.setFromYear(fromYear);
        boardSplitEntity.setToMonth(toMonth);
        boardSplitEntity.setToYear(toYear);
        boardSplitEntity.setValue1(value);

        BoardSplitEntity out = boardSplitRepository.save(boardSplitEntity);
        log.info("board split rule created. Board={}, rule={}", boardId, out.getId());
        return mapper.map(out);
    }

    public void deleteBoardEntry(UUID boardId, UUID boardEntryId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_ENTRY_EDIT);
        Optional<BoardEntryEntity> entry = boardEntryRepository.findById(boardEntryId);
        if (entry.isEmpty()) {
            throw new IllegalArgumentException("entry doesn't exist");
        }
        BoardEntryEntity value = entry.get();
        if (!value.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("entry not related to this board. Entry=" + boardEntryId + ", Board=" + boardId);
        }
        boardEntryRepository.deleteById(boardEntryId);
    }

    public void deleteBoardInvite(UUID boardId, UUID id) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_MANAGE_MEMBER);

        BoardInviteEntity entry = boardInviteRepository.findById(id)
                                                       .orElseThrow(() -> new IllegalArgumentException("Entry " + id + " doesn't exist."));

        UUID entryBoardId = entry.getBoard().getId();
        if (!entryBoardId.equals(boardId)) {
            throw new IllegalArgumentException("Entry " + id + " referenced board " + entryBoardId + " is not " + boardId);
        }
        boardInviteRepository.delete(entry);
        log.info("DELETE_BOARD_INVITE board={}, invite={}", entryBoardId, id);
    }

    public void deleteBoardSplit(UUID boardId, UUID boardSplitId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_EDIT);
        Optional<BoardSplitEntity> entry = boardSplitRepository.findById(boardSplitId);
        if (entry.isEmpty()) {
            throw new IllegalArgumentException("entry doesn't exist");
        }
        BoardSplitEntity value = entry.get();
        if (!value.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("entry not related to this board. Entry=" + boardSplitId + ", Board=" + boardId);
        }
        boardSplitRepository.deleteById(boardSplitId);
    }

    public Board getBoard(UUID id) {
        BoardEntity board = getBoardEntity(id);
        return mapper.map(board);
    }

    public List<MonthlyUserAnalysis> getBoardAnalysisByMonthUser(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<BoardEntryGroupByMonthUserCategory> entries =
                boardEntryRepository.getAggregatedBoardEntriesByMonthUserCategory(boardId);
        List<BoardSplit> splits = doGetBoardSplits(boardId);
        return aggregatedAnalysisService.getBoardAnalysisByMonthUser(entries, splits);
    }

    public BoardEntity getBoardEntity(UUID id) {
        boardPermissionService.checkGrant(id, BoardGrant.BOARD_VIEW);
        return boardRepository.findById(id).get();
    }

    public List<BoardEntry> getBoardEntries(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<BoardEntryEntity> entries = boardEntryRepository.findByBoardId(boardId);
        return StreamSupport.stream(entries.spliterator(), false).map(mapper::map).collect(Collectors.toList());
    }

    public BoardEntry getBoardEntry(UUID boardId, UUID boardEntryId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_ENTRY_EDIT);
        Optional<BoardEntryEntity> entry = boardEntryRepository.findById(boardEntryId);
        if (entry.isEmpty()) {
            throw new IllegalArgumentException("entry doesn't exist");
        }
        BoardEntryEntity value = entry.get();
        if (!value.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("entry not related to this board. Entry=" + boardEntryId + ", Board=" + boardId);
        }
        return mapper.map(value);
    }

    public List<BoardInvite> getBoardInvites(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_MANAGE_MEMBER);
        List<BoardInviteEntity> invites = boardInviteRepository.findByBoardId(boardId);
        return invites.stream().map(mapper::map).collect(Collectors.toList());
    }

    public List<BoardSplit> getBoardSplits(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        return doGetBoardSplits(boardId);
    }

    public List<BoardUser> getBoardUsers(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<BoardUserEntity> boardUsers = boardUserRepository.findByBoard_Id(boardId);
        return mapper.map(boardUsers);
    }

    public List<String> getCategories(UUID boardId) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_VIEW);
        List<String> categories = boardEntryRepository.findCategories(boardId);
        categories.sort((a, b) -> a.compareTo(b));
        return categories;
    }

    public List<Board> getVisibleBoards() {
        Iterable<BoardEntity> boards = boardRepository.findVisibleBoards(userService.getCurrentUser().getId());
        return StreamSupport.stream(boards.spliterator(), false) //
                            .map(board -> mapper.map(board)) //
                            .collect(Collectors.toList());
    }

    @Transactional
    public BoardEntry updateBoardEntry(UUID boardId, BoardEntry boardEntry) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_ENTRY_EDIT);
        log.info("Current user can edit board entries");

        UserEntity author = userService.getUserEntity(boardEntry.getOwnerId());
        boardPermissionService.checkGrant(author, boardId, BoardGrant.BOARD_ENTRY_EDIT);
        log.info("Author {} can edit board entries", author.getId());

        BoardEntryEntity entry = boardEntryRepository.findById(boardEntry.getId()).get();
        entry.setDate(boardEntry.getDate());
        entry.setLastUpdate(LocalDateTime.now());
        entry.setOwner(author);
        entry.setCategory(boardEntry.getCategory());
        entry.setDescription(boardEntry.getDescription());
        entry.setAmount(boardEntry.getAmount());
        BoardEntryEntity newEntry = boardEntryRepository.save(entry);
        log.info("Updated boardEntry. board={}, ownerId={}, entryId={}", boardId, boardEntry.getOwnerId(),
                newEntry.getId());
        return mapper.map(newEntry);
    }

    @Transactional
    public BoardSplit updateBoardSplit(UUID boardId, BoardSplit boardSplit) {
        boardPermissionService.checkGrant(boardId, BoardGrant.BOARD_EDIT);
        log.info("Current user can edit board splits");

        BoardSplitEntity entry = boardSplitRepository.findById(boardSplit.getId()).get();
        entry.setToMonth(boardSplit.getToMonth());
        entry.setFromMonth(boardSplit.getFromMonth());
        entry.setToYear(boardSplit.getToYear());
        entry.setFromYear(boardSplit.getFromYear());
        entry.setValue1(boardSplit.getValue1());
        UserEntity user = boardUserRepository.findUserBoard(boardId, boardSplit.getUserId()).getUser();
        entry.setUser(user);
        BoardSplitEntity newEntry = boardSplitRepository.save(entry);
        log.info("Updated boardSplit. board={}, entryId={}", boardId, newEntry.getId());
        return mapper.map(newEntry);
    }

    @Transactional
    public void useBoardInvite(UUID boardId, UUID inviteId) {
        log.info("Use invite board={}, invite={}", boardId, inviteId);
        BoardInviteEntity invite = boardInviteRepository.findById(inviteId).get();
        if (invite == null) {
            throw new IllegalArgumentException("Invite doesn't exist");
        }
        if (!invite.getBoard().getId().equals(boardId)) {
            log.info("Using an invite of a different board. expected: {}, actual: {}");
            throw new IllegalArgumentException("Invite and board don't match");
        }
        UserEntity currentUser = userService.getCurrentUserEntity();

        BoardUserEntity membership = boardUserRepository.findUserBoard(boardId, currentUser.getId());
        if (membership != null) {
            log.debug("User {} is already member of board {} with role {}", currentUser.getId(), boardId,
                    membership.getRole());
            return;
        }

        log.info("ADD_BOARD_USER board={}, role={}, invite={}", boardId, BoardUserRole.MEMBER, inviteId);
        BoardUserEntity entity = new BoardUserEntity();
        entity.setBoard(invite.getBoard());
        entity.setUser(currentUser);
        entity.setRole(BoardUserRole.MEMBER.name());
        boardUserRepository.save(entity);
    }

    protected List<BoardSplit> doGetBoardSplits(UUID boardId) {
        List<BoardSplitEntity> entries = boardSplitRepository.findByBoardId(boardId);
        return entries.stream().map(e -> mapper.map(e)).collect(Collectors.toList());
    }

    protected BigDecimal sum(Stream<BigDecimal> values) {
        return values.reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

}
