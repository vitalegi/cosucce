package it.vitalegi;

import it.vitalegi.cosucce.board.constant.BoardUserRole;
import it.vitalegi.cosucce.board.entity.BoardEntity;
import it.vitalegi.cosucce.board.entity.BoardEntryEntity;
import it.vitalegi.cosucce.board.entity.BoardInviteEntity;
import it.vitalegi.cosucce.board.entity.BoardSplitEntity;
import it.vitalegi.cosucce.board.entity.BoardUserEntity;
import it.vitalegi.cosucce.board.repository.BoardEntryRepository;
import it.vitalegi.cosucce.board.repository.BoardInviteRepository;
import it.vitalegi.cosucce.board.repository.BoardRepository;
import it.vitalegi.cosucce.board.repository.BoardSplitRepository;
import it.vitalegi.cosucce.board.repository.BoardUserRepository;
import it.vitalegi.cosucce.spando.entity.SpandoEntryEntity;
import it.vitalegi.cosucce.spando.repository.SpandoEntryRepository;
import it.vitalegi.cosucce.user.entity.UserEntity;
import it.vitalegi.cosucce.user.entity.UserOtpEntity;
import it.vitalegi.cosucce.user.repository.UserOtpRepository;
import it.vitalegi.cosucce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
class DatabaseConstraintsTests {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserOtpRepository userOtpRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardUserRepository boardUserRepository;
    @Autowired
    BoardEntryRepository boardEntryRepository;
    @Autowired
    BoardSplitRepository boardSplitRepository;
    @Autowired
    BoardInviteRepository boardInviteRepository;
    @Autowired
    SpandoEntryRepository spandoEntryRepository;

    UserEntity user1;
    UserEntity user2;
    UserOtpEntity userOtp1;
    UserOtpEntity userOtp2;
    BoardEntity board0;
    BoardUserEntity boardUser0_1;
    BoardUserEntity boardUser0_2;
    BoardEntryEntity boardEntry0_1;
    BoardEntryEntity boardEntry0_2;
    BoardSplitEntity boardSplit0_1;
    BoardSplitEntity boardSplit0_2;
    BoardInviteEntity boardInvite;
    SpandoEntryEntity spandoEntry1;
    SpandoEntryEntity spandoEntry2;

    @BeforeEach
    public void init() {
        user1 = userRepository.save(user("uid1", "username1"));
        user2 = userRepository.save(user("uid2", "username2"));
        board0 = boardRepository.save(board("board"));
        boardUser0_1 = boardUserRepository.save(boardUser(board0, user1, BoardUserRole.OWNER));
        boardUser0_2 = boardUserRepository.save(boardUser(board0, user2, BoardUserRole.MEMBER));
        boardEntry0_1 = boardEntryRepository.save(boardEntry(board0, user1, BigDecimal.ONE));
        boardEntry0_2 = boardEntryRepository.save(boardEntry(board0, user2, BigDecimal.ONE));
        boardSplit0_1 = boardSplitRepository.save(boardSplit(board0, user1));
        boardSplit0_2 = boardSplitRepository.save(boardSplit(board0, user2));
        boardInvite = boardInviteRepository.save(boardInvite(board0, user1));
        userOtp1 = userOtpRepository.save(userOtp(user1));
        userOtp2 = userOtpRepository.save(userOtp(user2));
        spandoEntry1 = spandoEntryRepository.save(spandoEntry(user1));
        spandoEntry2 = spandoEntryRepository.save(spandoEntry(user2));
    }

    @DisplayName("WHEN a board is deleted THEN board's resouces should be deleted")
    @Test
    public void test_deleteBoard_removeConnectedResources() {
        boardRepository.delete(board0);
        assertFalse(boardRepository.findById(board0.getId()).isPresent());
        assertFalse(boardUserRepository.findById(boardUser0_1.getId()).isPresent());
        assertFalse(boardUserRepository.findById(boardUser0_2.getId()).isPresent());
        assertFalse(boardEntryRepository.findById(boardEntry0_1.getId()).isPresent());
        assertFalse(boardEntryRepository.findById(boardEntry0_2.getId()).isPresent());
        assertFalse(boardSplitRepository.findById(boardSplit0_1.getId()).isPresent());
        assertFalse(boardSplitRepository.findById(boardSplit0_2.getId()).isPresent());
        assertFalse(boardInviteRepository.findById(boardInvite.getId()).isPresent());
        assertTrue(userOtpRepository.findById(userOtp1.getId()).isPresent());
        assertTrue(userOtpRepository.findById(userOtp2.getId()).isPresent());
    }

    @DisplayName("WHEN a user is deleted THEN board's resouces should be deleted")
    @Test
    public void test_deleteUser_removeConnectedResources() {
        userRepository.delete(user1);
        assertTrue(boardRepository.findById(board0.getId()).isPresent());
        assertFalse(boardUserRepository.findById(boardUser0_1.getId()).isPresent());
        assertTrue(boardUserRepository.findById(boardUser0_2.getId()).isPresent());
        assertFalse(boardEntryRepository.findById(boardEntry0_1.getId()).isPresent());
        assertTrue(boardEntryRepository.findById(boardEntry0_2.getId()).isPresent());
        assertFalse(boardSplitRepository.findById(boardSplit0_1.getId()).isPresent());
        assertTrue(boardSplitRepository.findById(boardSplit0_2.getId()).isPresent());
        assertFalse(boardInviteRepository.findById(boardInvite.getId()).isPresent());
        assertFalse(userOtpRepository.findById(userOtp1.getId()).isPresent());
        assertTrue(userOtpRepository.findById(userOtp2.getId()).isPresent());
    }

    BoardEntity board(String name) {
        BoardEntity board = new BoardEntity();
        board.setName(name);
        return board;
    }

    BoardEntryEntity boardEntry(BoardEntity board, UserEntity user, BigDecimal amount) {
        var entry = new BoardEntryEntity();
        entry.setOwner(user);
        entry.setBoard(board);
        entry.setAmount(amount);
        entry.setCategory("category");
        entry.setDate(LocalDate.now());
        return entry;
    }

    BoardInviteEntity boardInvite(BoardEntity board, UserEntity user) {
        var entry = new BoardInviteEntity();
        entry.setOwner(user);
        entry.setBoard(board);
        entry.setCreationDate(LocalDateTime.now());
        entry.setLastUpdate(LocalDateTime.now());
        return entry;
    }

    BoardSplitEntity boardSplit(BoardEntity board, UserEntity user) {
        var entry = new BoardSplitEntity();
        entry.setBoard(board);
        entry.setUser(user);
        entry.setValue1(BigDecimal.ONE);
        return entry;
    }

    BoardUserEntity boardUser(BoardEntity board, UserEntity user, BoardUserRole role) {
        var boardUser = new BoardUserEntity();
        boardUser.setBoard(board);
        boardUser.setUser(user);
        boardUser.setRole(role.name());
        return boardUser;
    }

    SpandoEntryEntity spandoEntry(UserEntity user) {
        var spandoEntry = new SpandoEntryEntity();
        spandoEntry.setUser(user);
        spandoEntry.setEntryDate(LocalDate.now());
        spandoEntry.setType("TYPE");
        return spandoEntry;
    }

    UserEntity user(String uid, String username) {
        UserEntity user = new UserEntity();
        user.setUid(uid);
        user.setUsername(username);
        return user;
    }

    UserOtpEntity userOtp(UserEntity user) {
        var userOtp = new UserOtpEntity();
        userOtp.setUser(user);
        userOtp.setOtp("otp");
        return userOtp;
    }
}
