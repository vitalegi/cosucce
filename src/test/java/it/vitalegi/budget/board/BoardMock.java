package it.vitalegi.budget.board;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.user.UserMock;

import java.time.LocalDateTime;
import java.util.UUID;

public class BoardMock {
    public static BoardEntity newBoardEntity(String name, long ownerId) {
        BoardEntity entity = new BoardEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
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
