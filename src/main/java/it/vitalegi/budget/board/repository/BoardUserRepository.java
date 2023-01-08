package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.entity.BoardUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardUserRepository extends CrudRepository<BoardUserEntity, Long> {

    List<BoardUserEntity> findByBoard_Id(UUID boardId);
}
