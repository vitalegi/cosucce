package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends CrudRepository<BoardEntity, UUID> {

    List<BoardEntity> findByOwnerId(String ownerId);
}
