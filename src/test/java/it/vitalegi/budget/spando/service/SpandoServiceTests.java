package it.vitalegi.budget.spando.service;

import it.vitalegi.budget.spando.constant.SpandoDay;
import it.vitalegi.budget.spando.dto.SpandoDays;
import it.vitalegi.budget.spando.entity.SpandoEntryEntity;
import it.vitalegi.budget.spando.mapper.SpandoMapper;
import it.vitalegi.budget.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpandoServiceTests {

    SpandoService spandoService;

    void checkDays(String expectedFrom, String expectedTo, SpandoDays actual) {
        assertEquals(LocalDate.parse(expectedFrom), actual.getFrom());
        assertEquals(LocalDate.parse(expectedTo), actual.getTo());
    }

    SpandoDays days(String from, String to) {
        SpandoDays entry = new SpandoDays();
        entry.setFrom(LocalDate.parse(from));
        entry.setTo(LocalDate.parse(to));
        return entry;
    }

    SpandoEntryEntity entry(String date) {
        SpandoEntryEntity entity = new SpandoEntryEntity();
        entity.setType(SpandoDay.SPANTO.name());
        entity.setEntryDate(LocalDate.parse(date));
        entity.setUser(new UserEntity());
        return entity;
    }

    @BeforeEach
    void init() {
        spandoService = new SpandoService();
        spandoService.mapper = new SpandoMapper();
    }

    @Test
    void test_getActivePeriod() {
        assertEquals(1, spandoService.getActivePeriod(days("2023-01-01", "2023-01-01")));
        assertEquals(2, spandoService.getActivePeriod(days("2023-01-01", "2023-01-02")));
        assertEquals(3, spandoService.getActivePeriod(days("2023-01-01", "2023-01-03")));
        assertEquals(4, spandoService.getActivePeriod(days("2023-01-01", "2023-01-04")));
    }

    @Test
    void test_getActivePeriod_2periods() {
        long period = spandoService.getActivePeriod(Arrays.asList(days("2023-03-01", "2023-03-02"), days("2023-03-04"
                , "2023-03-09")));
        assertEquals(4, period);
    }

    @Test
    void test_getActivePeriod_3periods() {
        long period = spandoService.getActivePeriod(Arrays.asList( //
                days("2023-03-01", "2023-03-02"), //
                days("2023-03-04", "2023-03-05"), //
                days("2023-03-09", "2023-03-10") //
        ));
        assertEquals(2, period);
    }

    @Test
    void test_getActivePeriod_multipleValues_shouldIgnoreOlderPeriods() {
        long period = spandoService.getActivePeriod(Arrays.asList( //
                days("2023-02-01", "2023-02-25"), //
                days("2023-03-01", "2023-03-02"), //
                days("2023-03-04", "2023-03-05"), //
                days("2023-03-07", "2023-03-08"), //
                days("2023-03-10", "2023-03-11"), //
                days("2023-03-13", "2023-03-14") //
        ));
        assertEquals(2, period);
    }

    @Test
    void test_getPeriod() {
        long period = spandoService.getPeriod(days("2023-03-01", "2023-03-05"), days("2023-03-07", "2023-03-10"));
        assertEquals(6, period);
    }

    @Test
    void test_getPeriods_2periods() {
        long period = spandoService.getPeriod(Arrays.asList(days("2023-03-01", "2023-03-02"), days("2023-03-04",
                "2023-03-05")));
        assertEquals(3, period);
    }

    @Test
    void test_getPeriods_3periods() {
        long period = spandoService.getPeriod(Arrays.asList(days("2023-03-01", "2023-03-02"), days("2023-03-04",
                "2023-03-05"), days("2023-03-09", "2023-03-10")));
        assertEquals(4, period);
    }

    @Test
    void test_getPeriods_multipleValues_shouldIgnoreOlderPeriods() {
        long period = spandoService.getPeriod(Arrays.asList( //
                days("2023-02-01", "2023-02-25"), //
                days("2023-03-01", "2023-03-02"), //
                days("2023-03-04", "2023-03-05"), //
                days("2023-03-07", "2023-03-08"), //
                days("2023-03-10", "2023-03-11"), //
                days("2023-03-13", "2023-03-14") //
        ));
        assertEquals(3, period);
    }

    @Test
    void test_getPeriods_notEnoughValues() {
        long period = spandoService.getPeriod(Collections.emptyList());
        assertEquals(SpandoService.DEFAULT_PERIOD, period);

        period = spandoService.getPeriod(Collections.singletonList(days("2023-03-01", "2023-03-02")));
        assertEquals(SpandoService.DEFAULT_PERIOD, period);
    }

    @DisplayName("GIVEN 2 spando days WHEN I ask for spando days THEN I receive the correct list")
    @Test
    void test_groupSpandoDays_connectedEntries_shouldReturnEntry() {
        List<SpandoDays> out = spandoService.groupSpandoDays(Arrays.asList(entry("2023-01-02"), entry("2023-01-01")));
        assertEquals(1, out.size());
        checkDays("2023-01-01", "2023-01-02", out.get(0));
    }

    @DisplayName("GIVEN multiple groups WHEN I ask for spando days THEN I receive the correct list")
    @Test
    void test_groupSpandoDays_multipleGroups_shouldReturnEntries() {
        List<SpandoDays> out = spandoService.groupSpandoDays(Arrays.asList(entry("2023-01-01"), entry("2023-01-02"),
                entry("2023-01-03"), //
                entry("2023-01-05"), //
                entry("2023-02-01"), entry("2023-02-02"), //
                entry("2023-03-01")));
        assertEquals(4, out.size());
        checkDays("2023-01-01", "2023-01-03", out.get(0));
        checkDays("2023-01-05", "2023-01-05", out.get(1));
        checkDays("2023-02-01", "2023-02-02", out.get(2));
        checkDays("2023-03-01", "2023-03-01", out.get(3));
    }

    @DisplayName("GIVEN no spando days WHEN I ask for spando days THEN I receive an empty list")
    @Test
    void test_groupSpandoDays_noEntries_shouldReturnEmptyList() {
        List<SpandoDays> out = spandoService.groupSpandoDays(Collections.emptyList());
        assertEquals(0, out.size());
    }

    @DisplayName("GIVEN 1 spando days WHEN I ask for spando days THEN I receive the correct list")
    @Test
    void test_groupSpandoDays_oneEntry_shouldReturnEntry() {
        List<SpandoDays> out = spandoService.groupSpandoDays(Arrays.asList(entry("2023-01-01")));
        assertEquals(1, out.size());
        checkDays("2023-01-01", "2023-01-01", out.get(0));
    }

    @DisplayName("GIVEN entries not sorted WHEN I ask for spando days THEN I receive the correct list")
    @Test
    void test_groupSpandoDays_unsortedEntries_shouldReturnEntries() {
        List<SpandoDays> out = spandoService.groupSpandoDays(Arrays.asList(entry("2023-01-03"), entry("2023-01-02"),
                entry("2023-01-01")));
        assertEquals(1, out.size());
        checkDays("2023-01-01", "2023-01-03", out.get(0));
    }
}
