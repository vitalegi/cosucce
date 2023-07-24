package it.vitalegi.cosucce.spando.repository;

import it.vitalegi.cosucce.spando.entity.SpandoEntryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpandoEntryRepository extends CrudRepository<SpandoEntryEntity, UUID> {
    List<SpandoEntryEntity> findByUserId(long userId);

}
