package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardUserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardUserRepository extends CrudRepository<BoardUserEntity, Long> {

    List<BoardUserEntity> findByBoard_Id(UUID boardId);

    @Query("SELECT bu FROM BoardUser bu WHERE bu.board.id=:boardId AND bu.user.id=:userId")
    BoardUserEntity findUserBoard(UUID boardId, long userId);
}
