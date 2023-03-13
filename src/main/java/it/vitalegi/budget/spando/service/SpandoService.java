package it.vitalegi.budget.spando.service;

import it.vitalegi.budget.exception.PermissionException;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.spando.constant.SpandoDay;
import it.vitalegi.budget.spando.dto.SpandoDays;
import it.vitalegi.budget.spando.dto.SpandoEntry;
import it.vitalegi.budget.spando.entity.SpandoEntryEntity;
import it.vitalegi.budget.spando.mapper.SpandoMapper;
import it.vitalegi.budget.spando.repository.SpandoEntryRepository;
import it.vitalegi.budget.user.entity.UserEntity;
import it.vitalegi.budget.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class SpandoService {

    @Autowired
    SpandoEntryRepository spandoEntryRepository;
    @Autowired
    UserService userService;

    @Autowired
    SpandoMapper mapper;

    @Transactional
    public SpandoEntry addOrUpdateSpandoEntry(LocalDate date) {
        UserEntity owner = userService.getCurrentUserEntity();
        var entries = spandoEntryRepository.findByUserId(owner.getId());
        var exists = entries.stream().filter(e -> e.getEntryDate().equals(date)).findFirst().orElse(null);
        if (exists == null) {
            return doAddSpandoEntry(date, SpandoDay.SPANTO, owner);
        }
        if (mapper.map(exists.getType()) == SpandoDay.SPANTO) {
            deleteSpandoEntry(date);
            return mapper.map(exists);
        } else {
            return doUpdateSpandoEntry(exists.getId(), date, SpandoDay.SPANTO, owner);
        }
    }

    public void deleteSpandoEntry(LocalDate date) {
        UserEntity owner = userService.getCurrentUserEntity();
        var entries = spandoEntryRepository.findByUserId(owner.getId());
        entries.stream().filter(e -> e.getEntryDate().equals(date)).forEach(e -> {
            spandoEntryRepository.delete(e);
            log.info("Deleted spando entry {}", e.getId());
        });
    }

    public List<SpandoDays> getSpandoDays() {
        long userId = userService.getCurrentUserEntity().getId();
        List<SpandoEntryEntity> entries = spandoEntryRepository.findByUserId(userId);
        return groupSpandoDays(entries);
    }

    protected void checkPermission(SpandoEntryEntity entry) {
        long ownerId = entry.getUser().getId();
        long userId = userService.getCurrentUserEntity().getId();
        if (ownerId != userId) {
            throw new PermissionException("spando", entry.getId().toString(), "EDIT");
        }
    }

    protected SpandoEntry doAddSpandoEntry(LocalDate date, SpandoDay type, UserEntity owner) {
        SpandoEntryEntity entity = new SpandoEntryEntity();
        entity.setEntryDate(date);
        entity.setType(type.name());
        entity.setUser(owner);
        SpandoEntryEntity saved = spandoEntryRepository.save(entity);
        log.info("SpandoEntry created. User={}, day={}", owner.getId(), saved.getEntryDate());
        return mapper.map(entity);
    }

    protected SpandoEntry doUpdateSpandoEntry(UUID id, LocalDate date, SpandoDay type, UserEntity owner) {
        SpandoEntryEntity entity = spandoEntryRepository.findById(id).orElseThrow();
        entity.setEntryDate(date);
        entity.setType(type.name());
        SpandoEntryEntity saved = spandoEntryRepository.save(entity);
        log.info("SpandoEntry updated. User={}, day={}", owner.getId(), saved.getEntryDate());
        return mapper.map(entity);
    }

    protected List<SpandoDays> groupSpandoDays(List<SpandoEntryEntity> entries) {
        List<SpandoDays> spandoDays = new ArrayList<>();

        entries.stream().map(mapper::map) //
               .filter(e -> e.getType().equals(SpandoDay.SPANTO)) //
               .sorted(Comparator.comparing(SpandoEntry::getDate)) //
               .forEach(entry -> merge(spandoDays, entry.getDate()));

        return spandoDays;
    }

    protected void merge(List<SpandoDays> spandoDays, LocalDate nextDate) {
        if (spandoDays.isEmpty()) {
            spandoDays.add(singleSpandoDay(nextDate));
            return;
        }
        SpandoDays lastEntry = spandoDays.get(spandoDays.size() - 1);
        if (lastEntry.getTo().plusDays(1).equals(nextDate)) {
            lastEntry.setTo(nextDate);
        } else {
            spandoDays.add(singleSpandoDay(nextDate));
        }
    }

    protected SpandoDays singleSpandoDay(LocalDate date) {
        SpandoDays spando = new SpandoDays();
        spando.setFrom(date);
        spando.setTo(date);
        return spando;
    }

}
