package it.vitalegi.cosucce.board.repository;

import it.vitalegi.cosucce.board.entity.BoardEntryEntity;
import it.vitalegi.cosucce.board.entity.BoardEntryGroupByMonth;
import it.vitalegi.cosucce.board.entity.BoardEntryGroupByMonthUserCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardEntryRepository extends CrudRepository<BoardEntryEntity, UUID> {

    List<BoardEntryEntity> findByBoardId(UUID boardId);
    
    @Query("SELECT DISTINCT be.category FROM BoardEntry be WHERE be.board.id = :boardId")
    List<String> findCategories(@Param("boardId") UUID boardId);

    @Query("SELECT new it.vitalegi.cosucce.board.entity.BoardEntryGroupByMonth(YEAR(be.date), " +
            "MONTH(be.date), SUM(be.amount)) " + //
            "FROM BoardEntry be WHERE be.board.id = :boardId " + //
            "GROUP BY YEAR(be.date), MONTH(be.date) " +
            "ORDER BY YEAR(be.date), MONTH(be.date)")
    List<BoardEntryGroupByMonth> getAggregatedBoardEntriesByMonth(@Param("boardId") UUID boardId);

    @Query("SELECT new it.vitalegi.cosucce.board.entity.BoardEntryGroupByMonthUserCategory(YEAR(be.date), " +
            "MONTH(be.date), be.owner.id, be.category, SUM(be.amount)) " + //
            "FROM BoardEntry be WHERE be.board.id = :boardId " + //
            "GROUP BY be.owner.id, be.category, YEAR(be.date), MONTH(be.date)")
    List<BoardEntryGroupByMonthUserCategory> getAggregatedBoardEntriesByMonthUserCategory(@Param("boardId") UUID boardId);
}
