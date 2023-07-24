package it.vitalegi.cosucce.board.repository;

import it.vitalegi.cosucce.board.entity.BoardSplitEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardSplitRepository extends CrudRepository<BoardSplitEntity, UUID> {

    List<BoardSplitEntity> findByBoardId(UUID boardId);
}
