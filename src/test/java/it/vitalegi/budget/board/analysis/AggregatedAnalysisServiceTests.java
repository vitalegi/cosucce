package it.vitalegi.budget.board.analysis;

import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.analysis.MonthlyUserAnalysis;
import it.vitalegi.budget.board.dto.analysis.UserAmount;
import it.vitalegi.budget.board.entity.BoardEntryGroupByMonthUserCategory;
import it.vitalegi.budget.board.service.AggregatedAnalysisService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@Log4j2
public class AggregatedAnalysisServiceTests {

    AggregatedAnalysisService analysisService;

    @BeforeEach
    public void init() {
        analysisService = new AggregatedAnalysisService();
    }

    @DisplayName("getAnalysisByMonth should return the list of expenses, actual and expected, by user, by month, " +
            "using the default split")
    @Test
    public void test_getAnalysisByMonth_defaultSplit_shouldWork() {
        List<BoardEntryGroupByMonthUserCategory> entries = new ArrayList<>();
        entries.add(entry(2023, 1, 1, "a", "1"));
        entries.add(entry(2023, 1, 2, "a", "1"));

        List<BoardSplit> splits = Arrays.asList(//
                split(1, "0.6"), //
                split(2, "0.4") //
        );
        List<MonthlyUserAnalysis> analysis = analysisService.getBoardAnalysisByMonthUser(entries, splits);
        assertEquals(1, analysis.size());
        MonthlyUserAnalysis entry = validateAndGetMonthlyUserAnalysis(2023, 1, 0, analysis);
        assertEquals(2, entry.getUsers()
                             .size());
        validateUserAmount(entry, 1, "1", "1.2", "-0.2");
        validateUserAmount(entry, 2, "1", "0.8", "0.2");
    }

    @DisplayName("getAnalysisByMonth should return the list of expenses, actual and expected, by user, by month, " +
            "using the most detailed split available")
    @Test
    public void test_getAnalysisByMonth_multipleSplits_shouldWork() {
        List<BoardEntryGroupByMonthUserCategory> entries = new ArrayList<>();
        LocalDate from = LocalDate.of(2020, 01, 01);
        LocalDate to = LocalDate.of(2020, 12, 01);

        entries.addAll(entries(from, to, 1, "", "1"));
        entries.addAll(entries(from, to, 2, "", "1"));

        List<BoardSplit> splits = Arrays.asList(//
                split(1, "0.6"), //
                split(2, "0.4"), //
                split(2, 2020, 05, null, null, "0.5"), //
                split(1, 2020, 05, 2020, 12, "0.5"));

        List<MonthlyUserAnalysis> analysis = analysisService.getBoardAnalysisByMonthUser(entries, splits);
        assertEquals(12, analysis.size());

        MonthlyUserAnalysis entry;

        entry = validateAndGetMonthlyUserAnalysis(2020, 1, 0, analysis);
        validateUserAmount(entry, 1, "1", "1.2", "-0.2");
        validateUserAmount(entry, 2, "1", "0.8", "0.2");

        entry = validateAndGetMonthlyUserAnalysis(2020, 2, 1, analysis);
        validateUserAmount(entry, 1, "1", "1.2", "-0.4");
        validateUserAmount(entry, 2, "1", "0.8", "0.4");

        entry = validateAndGetMonthlyUserAnalysis(2020, 3, 2, analysis);
        validateUserAmount(entry, 1, "1", "1.2", "-0.6");
        validateUserAmount(entry, 2, "1", "0.8", "0.6");

        entry = validateAndGetMonthlyUserAnalysis(2020, 4, 3, analysis);
        validateUserAmount(entry, 1, "1", "1.2", "-0.8");
        validateUserAmount(entry, 2, "1", "0.8", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 5, 4, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 6, 5, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 7, 6, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 8, 7, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 9, 8, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 10, 9, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 11, 10, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");

        entry = validateAndGetMonthlyUserAnalysis(2020, 12, 11, analysis);
        validateUserAmount(entry, 1, "1", "1.0", "-0.8");
        validateUserAmount(entry, 2, "1", "1.0", "0.8");
    }

    @Disabled("Make test non blocker")
    @DisplayName("getAnalysisByMonth - performance analysis - 20 years, 2 users - should complete in time")
    @Test
    public void test_getAnalysisByMonth_performanceTest1() {
        performanceTest(20, 2, 100);
    }

    @Disabled("Make test non blocker")
    @DisplayName("getAnalysisByMonth - performance analysis - 10 years, 10 users - should complete in time")
    @Test
    public void test_getAnalysisByMonth_performanceTest2() {
        performanceTest(10, 10, 100);
    }

    @Disabled("Make test non blocker")
    @DisplayName("getAnalysisByMonth - performance analysis - 20 years, 20 users - should complete in time")
    @Test
    public void test_getAnalysisByMonth_performanceTest3() {
        performanceTest(20, 20, 200);
    }

    @DisplayName("getAnalysisByMonth should return the list of expenses, actual and expected, by user, by month")
    @Test
    public void test_getAnalysisByMonth_shouldWork() {
        List<BoardEntryGroupByMonthUserCategory> entries = new ArrayList<>();
        entries.add(entry(2023, 1, 1, "a", "1"));
        entries.add(entry(2023, 1, 2, "a", "1"));

        List<BoardSplit> splits = Arrays.asList(//
                split(1, "0.5"), //
                split(2, "0.5") //
        );
        List<MonthlyUserAnalysis> analysis = analysisService.getBoardAnalysisByMonthUser(entries, splits);
        assertEquals(1, analysis.size());
        MonthlyUserAnalysis entry = validateAndGetMonthlyUserAnalysis(2023, 1, 0, analysis);
        assertEquals(2, entry.getUsers()
                             .size());
        validateUserAmount(entry, 1, "1", "1", "0");
        validateUserAmount(entry, 2, "1", "1", "0");
    }

    protected List<LocalDate> months(LocalDate firstDate, LocalDate lastDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = firstDate;
        while (!currentDate.isAfter(lastDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusMonths(1);
        }
        return dates;
    }

    protected List<Long> userIds(int n) {
        return LongStream.range(0, n)
                         .mapToObj(v -> v)
                         .collect(Collectors.toList());
    }

    List<BoardEntryGroupByMonthUserCategory> entries(LocalDate fromDate, LocalDate toDate, long userId,
                                                     String category, String value) {
        return months(fromDate, toDate).stream()
                                       .map(date -> entry(date.getYear(), date.getMonthValue(), userId,
                                               category, value))
                                       .collect(Collectors.toList());
    }

    BoardEntryGroupByMonthUserCategory entry(int year, int month, long userId, String category, String value) {
        return new BoardEntryGroupByMonthUserCategory(year, month, userId, category, new BigDecimal(value));
    }

    UserAmount getUserAmount(MonthlyUserAnalysis analysis, long userId) {
        UserAmount value = analysis.getUsers()
                                   .stream()
                                   .filter(u -> u.getUserId() == userId)
                                   .findFirst()
                                   .orElse(null);
        if (value != null) {
            return value;
        }
        throw new NullPointerException("Cannot find " + userId + " in " + analysis);
    }

    void performanceTest(int years, int users, int ms) {
        assertTimeout(Duration.ofMillis(ms), () -> performanceTest(years, users));
    }

    void performanceTest(int years, int users) {
        List<BoardEntryGroupByMonthUserCategory> entries = new ArrayList<>();
        List<BoardSplit> splits = new ArrayList<>();

        LocalDate from = LocalDate.of(2020, 01, 01);
        LocalDate to = LocalDate.of(2020 + years, 01, 01);

        BigDecimal ratio = BigDecimal.ONE.divide(BigDecimal.valueOf(users), MathContext.DECIMAL32);
        List<Long> ids = userIds(users);
        ids.forEach(id -> {
            entries.addAll(entries(from, to, id, "", "1"));
            splits.add(split(id, ratio));
        });
        analysisService.getBoardAnalysisByMonthUser(entries, splits);
    }

    BoardSplit split(long userId, String value) {
        return split(userId, null, null, null, null, new BigDecimal(value));
    }

    BoardSplit split(long userId, Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth,
                     BigDecimal value) {
        BoardSplit split = new BoardSplit();
        split.setId(UUID.randomUUID());
        split.setBoardId(UUID.randomUUID());
        split.setUserId(userId);
        split.setFromYear(fromYear);
        split.setFromMonth(fromMonth);
        split.setToYear(toYear);
        split.setToMonth(toMonth);
        split.setValue1(value);
        return split;
    }

    BoardSplit split(long userId, Integer fromYear, Integer fromMonth, Integer toYear, Integer toMonth, String value) {
        return split(userId, fromYear, fromMonth, toYear, toMonth, new BigDecimal(value));
    }

    BoardSplit split(long userId, BigDecimal value) {
        return split(userId, null, null, null, null, value);
    }

    MonthlyUserAnalysis validateAndGetMonthlyUserAnalysis(int year, int month, int expectedIndex,
                                                          List<MonthlyUserAnalysis> analysis) {
        log.info("Expect to find year={}, month={} on index={}", year, month, expectedIndex);
        MonthlyUserAnalysis obj = analysis.get(expectedIndex);
        assertEquals(year, obj.getYear());
        assertEquals(month, obj.getMonth());
        return obj;
    }

    void validateUserAmount(MonthlyUserAnalysis analysis, long userId, String actual, String expected,
                            String cumulatedCredit) {
        UserAmount u = getUserAmount(analysis, userId);
        assertEquals(userId, u.getUserId());

        BigDecimal actualValue = new BigDecimal(actual);
        BigDecimal expectedValue = new BigDecimal(expected);
        BigDecimal cumulatedCreditValue = new BigDecimal(cumulatedCredit);

        assertEquals(0, actualValue.compareTo(u.getActual()),
                "Actual values are not equals. Expected=" + actualValue.toPlainString() + " Actual=" + u.getActual()
                                                                                                        .toPlainString());
        assertEquals(0, expectedValue.compareTo(u.getExpected()),
                "Expected values are not equals. Expected=" + expectedValue.toPlainString() + " Actual=" + u.getExpected()
                                                                                                            .toPlainString());
        assertEquals(0, cumulatedCreditValue.compareTo(u.getCumulatedCredit()), "Expected values are not equals. " +
                "Expected=" + cumulatedCreditValue.toPlainString() + " Actual=" + u.getCumulatedCredit()
                                                                                   .toPlainString());
    }
}
