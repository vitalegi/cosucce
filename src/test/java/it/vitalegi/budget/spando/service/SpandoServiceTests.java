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
