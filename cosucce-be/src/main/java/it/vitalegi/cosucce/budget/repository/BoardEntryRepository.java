package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BoardEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardEntryRepository extends JpaRepository<BoardEntryEntity, UUID> {
    List<BoardEntryEntity> findAllByBoardId(UUID boardId);

}
