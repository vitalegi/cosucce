package it.vitalegi.cosucce.budget.repository;

import it.vitalegi.cosucce.budget.entity.BoardUserEntity;
import it.vitalegi.cosucce.budget.entity.BoardUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardUserRepository extends JpaRepository<BoardUserEntity, BoardUserId> {
}
