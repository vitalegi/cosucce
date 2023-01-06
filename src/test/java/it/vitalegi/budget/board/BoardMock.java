package it.vitalegi.budget.board;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class BoardMock {
    public static BoardEntity newBoardEntity(String name, String ownerId) {
        BoardEntity entity = new BoardEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();
        entity.setLastUpdate(now);
        entity.setCreationDate(now);
        return entity;
    }

    public static Board emptyBoard() {
        Board board = new Board();
        board.setId(UUID.randomUUID());
        return board;
    }
}
