package it.vitalegi.budget.board;

import it.vitalegi.budget.board.dto.Board;
import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.mapper.BoardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardMapperTests {

    BoardMapper mapper;

    @BeforeEach
    void init() {
        mapper = new BoardMapper();
    }

    @Test
    void test_map() {
        BoardEntity entity = BoardMock.newBoardEntity("name", 1);
        Board dto = mapper.map(entity);
        assertEquals(entity, mapper.map(dto));
    }
}
