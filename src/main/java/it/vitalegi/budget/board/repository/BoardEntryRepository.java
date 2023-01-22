package it.vitalegi.budget.board.repository;

import it.vitalegi.budget.board.entity.BoardEntryEntity;
import it.vitalegi.budget.board.repository.util.BoardEntryGroupByMonthUserCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardEntryRepository extends CrudRepository<BoardEntryEntity, UUID> {

    List<BoardEntryEntity> findByBoardId(UUID boardId);

    List<BoardEntryEntity> findByOwnerId(String ownerId);

    @Query("SELECT DISTINCT be.category FROM BoardEntry be WHERE be.board.id = :boardId")
    List<String> findCategories(@Param("boardId") UUID boardId);

    @Query("SELECT new it.vitalegi.budget.board.repository.util.BoardEntryGroupByMonthUserCategory(YEAR(be.date), MONTH(be.date), be.owner.id, be.category, SUM(be.amount)) " + //
            "FROM BoardEntry be WHERE be.board.id = :boardId " + //
            "GROUP BY be.owner.id, be.category, YEAR(be.date), MONTH(be.date)")
    List<BoardEntryGroupByMonthUserCategory> getAggregatedBoardEntriesByMonthUserCategory(@Param("boardId") UUID boardId);
}
