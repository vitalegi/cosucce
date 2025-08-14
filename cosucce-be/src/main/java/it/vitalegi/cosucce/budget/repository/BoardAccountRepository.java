package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BoardAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardAccountRepository extends JpaRepository<BoardAccountEntity, UUID> {

    List<BoardAccountEntity> findAllByBoardId(UUID boardId);
}
