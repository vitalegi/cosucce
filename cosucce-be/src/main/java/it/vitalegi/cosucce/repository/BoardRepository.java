package it.vitalegi.cosucce.repository;

import it.vitalegi.cosucce.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, String> {

    BoardEntity findByName(String name);
}
