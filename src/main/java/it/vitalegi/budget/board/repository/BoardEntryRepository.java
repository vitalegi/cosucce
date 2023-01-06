package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardEntryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardEntryRepository extends CrudRepository<BoardEntryEntity, UUID> {

    List<BoardEntryEntity> findByBoardId(UUID boardId);

    List<BoardEntryEntity> findByOwnerId(String ownerId);

}
