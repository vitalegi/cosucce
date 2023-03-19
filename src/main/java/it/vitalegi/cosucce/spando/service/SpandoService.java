package it.vitalegi.cosucce.spando.service;

import it.vitalegi.cosucce.exception.PermissionException;
import it.vitalegi.cosucce.metrics.Performance;
import it.vitalegi.cosucce.metrics.Type;
import it.vitalegi.cosucce.spando.constant.SpandoDay;
import it.vitalegi.cosucce.spando.dto.SpandoDays;
import it.vitalegi.cosucce.spando.dto.SpandoEntry;
import it.vitalegi.cosucce.spando.entity.SpandoEntryEntity;
import it.vitalegi.cosucce.spando.mapper.SpandoMapper;
import it.vitalegi.cosucce.spando.repository.SpandoEntryRepository;
import it.vitalegi.cosucce.user.entity.UserEntity;
import it.vitalegi.cosucce.user.service.UserService;
import it.vitalegi.cosucce.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class SpandoService {
    static final int MAX_PERIODS = 5;
    static final int DEFAULT_PERIOD = 28;
    static final int DEFAULT_ACTIVE_PERIOD = 5;

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
            var newPeriod = isNewPeriod(date);
            var newEntry = doAddSpandoEntry(date, SpandoDay.SPANTO, owner);
            if (newPeriod) {
                initializeNewPeriod(date, owner);
            }
            return newEntry;
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

    public List<SpandoDays> getSpandoEstimates() {
        long userId = userService.getCurrentUserEntity().getId();
        List<SpandoEntryEntity> entries = spandoEntryRepository.findByUserId(userId);
        List<SpandoDays> spandoDays = groupSpandoDays(entries);
        if (spandoDays.isEmpty()) {
            return Collections.emptyList();
        }
        long period = getPeriod(spandoDays);
        long activePeriod = getActivePeriod(spandoDays);
        LocalDate lastPeriodStart = spandoDays.get(spandoDays.size() - 1).getFrom();
        return createEstimate(period, activePeriod, lastPeriodStart);
    }

    protected void checkPermission(SpandoEntryEntity entry) {
        long ownerId = entry.getUser().getId();
        long userId = userService.getCurrentUserEntity().getId();
        if (ownerId != userId) {
            throw new PermissionException("spando", entry.getId().toString(), "EDIT");
        }
    }

    protected List<SpandoDays> createEstimate(long period, long activePeriod, LocalDate lastKnownPeriodStart) {
        List<SpandoDays> estimates = new ArrayList<>();
        LocalDate periodStart = lastKnownPeriodStart;
        for (int i = 0; i < 12 * 5; i++) {
            periodStart = periodStart.plusDays(period);
            SpandoDays days = new SpandoDays();
            days.setFrom(periodStart);
            days.setTo(periodStart.plusDays(activePeriod - 1));
            estimates.add(days);
        }
        return estimates;
    }

    protected long days(LocalDate from, LocalDate to) {
        return DAYS.between(from, to);
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

    protected long getActivePeriod(SpandoDays spando1) {
        return days(spando1.getFrom(), spando1.getTo()) + 1;
    }

    protected long getActivePeriod(List<SpandoDays> spandoDays) {
        if (spandoDays.size() <= 1) {
            return DEFAULT_ACTIVE_PERIOD;
        }
        if (spandoDays.size() > MAX_PERIODS) {
            spandoDays = spandoDays.subList(spandoDays.size() - MAX_PERIODS, spandoDays.size());
        }
        long period = 0;
        for (int i = 0; i < spandoDays.size(); i++) {
            period += getActivePeriod(spandoDays.get(i));
        }
        return period / spandoDays.size();
    }

    protected long getPeriod(SpandoDays spando1, SpandoDays spando2) {
        LocalDate start = spando1.getFrom();
        LocalDate end = spando2.getFrom();
        return days(start, end);
    }

    protected long getPeriod(List<SpandoDays> spandoDays) {
        if (spandoDays.size() <= 1) {
            return DEFAULT_PERIOD;
        }
        if (spandoDays.size() > MAX_PERIODS) {
            spandoDays = spandoDays.subList(spandoDays.size() - MAX_PERIODS, spandoDays.size());
        }
        long period = 0;
        for (int i = 1; i < spandoDays.size(); i++) {
            period += getPeriod(spandoDays.get(i - 1), spandoDays.get(i));
        }
        return period / (spandoDays.size() - 1);
    }

    protected List<SpandoDays> groupSpandoDays(List<SpandoEntryEntity> entries) {
        List<SpandoDays> spandoDays = new ArrayList<>();

        entries.stream().map(mapper::map) //
               .filter(e -> e.getType().equals(SpandoDay.SPANTO)) //
               .sorted(Comparator.comparing(SpandoEntry::getDate)) //
               .forEach(entry -> merge(spandoDays, entry.getDate()));

        return spandoDays;
    }

    protected void initializeNewPeriod(LocalDate startDate, UserEntity owner) {
        for (int i = 1; i <= 4; i++) {
            doAddSpandoEntry(startDate.plusDays(i), SpandoDay.SPANTO, owner);
        }
    }

    protected boolean isNewPeriod(LocalDate date) {
        long userId = userService.getCurrentUserEntity().getId();
        List<SpandoEntryEntity> entries = spandoEntryRepository.findByUserId(userId);
        return entries.stream().noneMatch(e -> DateUtil.daysBetween(e.getEntryDate(), date) < 5);
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
