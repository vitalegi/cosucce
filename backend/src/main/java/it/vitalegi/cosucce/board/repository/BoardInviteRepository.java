package it.vitalegi.cosucce.board.repository;

import it.vitalegi.cosucce.board.entity.BoardInviteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardInviteRepository extends CrudRepository<BoardInviteEntity, UUID> {

    List<BoardInviteEntity> findByBoardId(UUID boardId);

}
