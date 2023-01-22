package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardSplitEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardSplitRepository extends CrudRepository<BoardSplitEntity, UUID> {

    List<BoardSplitEntity> findByBoardId(UUID boardId);
}
