package it.vitalegi.cosucce.board.repository;

import it.vitalegi.cosucce.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends CrudRepository<BoardEntity, UUID> {

    @Query("SELECT b FROM Board b INNER JOIN b.boardUsers bu WHERE bu.user.id=:userId")
    List<BoardEntity> findVisibleBoards(@Param("userId") long ownerId);

}
