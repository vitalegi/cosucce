package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardEntity;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends CrudRepository<BoardEntity, UUID> {

    //@Query("SELECT b FROM Board b WHERE b.owner.id=:userId")
    //List<BoardEntity> findByOwnerId2(@Param("userId") long ownerId);

    List<BoardEntity> findByOwner_Id(@Param("userId") long ownerId);
}
