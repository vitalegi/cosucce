package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, UUID> {

    BoardEntity findByName(String name);

    @Query("SELECT b FROM board b JOIN b.boardUsers bu WHERE bu.user.id = :userId ORDER BY b.lastUpdate")
    List<BoardEntity> findAllByUser(@Param("userId") UUID userId);
}
