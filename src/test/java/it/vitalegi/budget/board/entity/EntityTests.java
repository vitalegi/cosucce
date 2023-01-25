package it.vitalegi.budget.board.entity;

import it.vitalegi.budget.user.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EntityTests {

    @DisplayName("UserEntity.equals test suite")
    @Test
    void test_UserEntity_equals() {
        test_equals(user(10), user(10), user(20), new EntityTests());
    }

    <E> void test_equals(E base, E equalToBase, E different, Object randomObj) {
        assertEquals(base, base);
        assertEquals(base, equalToBase);
        assertEquals(equalToBase, base);
        assertNotEquals(null, base);
        assertNotEquals(base, different);
        assertNotEquals(base, randomObj);
    }

    UserEntity user(long id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }

    @DisplayName("UserEntity.hash test suite")
    @Test
    void test_UserEntity_hash() {
        test_hash(user(10), user(10));
    }

    <E> void test_hash(E base, E equalToBase) {
        assertEquals(base.hashCode(), base.hashCode());

        assertEquals(base, equalToBase);
        assertEquals(base.hashCode(), equalToBase.hashCode());
    }

    @DisplayName("BoardEntity.equals test suite")
    @Test
    void test_BoardEntity_equals() {
        UUID id1 = UUID.randomUUID();
        UUID id1_1 = UUID.fromString(id1.toString());
        UUID id2 = UUID.randomUUID();
        test_equals(board(id1), board(id1_1), board(id2), new EntityTests());
    }

    BoardEntity board(UUID id) {
        BoardEntity e = new BoardEntity();
        e.setId(id);
        return e;
    }

    @DisplayName("BoardEntity.hash test suite")
    @Test
    void test_BoardEntity_hash() {
        UUID id1 = UUID.randomUUID();
        UUID id1_1 = UUID.fromString(id1.toString());
        test_hash(board(id1), board(id1_1));
    }

    @DisplayName("BoardEntryEntity.equals test suite")
    @Test
    void test_BoardEntryEntity_equals() {
        UUID id1 = UUID.randomUUID();
        UUID id1_1 = UUID.fromString(id1.toString());
        UUID id2 = UUID.randomUUID();
        test_equals(boardEntry(id1), boardEntry(id1_1), boardEntry(id2), new EntityTests());
    }

    BoardEntryEntity boardEntry(UUID id) {
        BoardEntryEntity e = new BoardEntryEntity();
        e.setId(id);
        return e;
    }

    @DisplayName("BoardEntryEntity.hash test suite")
    @Test
    void test_BoardEntryEntity_hash() {
        UUID id1 = UUID.randomUUID();
        UUID id1_1 = UUID.fromString(id1.toString());
        test_hash(boardEntry(id1), boardEntry(id1_1));
    }

    @DisplayName("BoardUserEntity.equals test suite")
    @Test
    void test_BoardUserEntity_equals() {
        test_equals(boardUser(10), boardUser(10), boardUser(20), new EntityTests());
    }

    BoardUserEntity boardUser(long id) {
        BoardUserEntity e = new BoardUserEntity();
        e.setId(id);
        return e;
    }

    @DisplayName("BoardUserEntity.hash test suite")
    @Test
    void test_BoardUserEntity_hash() {
        test_hash(boardUser(10), boardUser(10));
    }
}
