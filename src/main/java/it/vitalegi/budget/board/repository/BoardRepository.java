package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface BoardRepository extends CrudRepository<BoardEntity, UUID> {

    List<BoardEntity> findByOwnerId(String ownerId);
}
