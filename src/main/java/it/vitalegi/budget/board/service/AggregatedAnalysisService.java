package it.vitalegi.budget.board.service;

import it.vitalegi.budget.board.dto.BoardSplit;
import it.vitalegi.budget.board.dto.analysis.MonthlyUserAnalysis;
import it.vitalegi.budget.board.dto.analysis.UserAmount;
import it.vitalegi.budget.board.entity.BoardEntryGroupByMonthUserCategory;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class AggregatedAnalysisService {

    public List<MonthlyUserAnalysis> getBoardAnalysisByMonthUser(List<BoardEntryGroupByMonthUserCategory> entries,
                                                                 List<BoardSplit> splits) {
        List<MonthlyUserAnalysis> data = new ArrayList<>();
        if (entries.isEmpty()) {
            return data;
        }

        LocalDate firstDate = getFirstDate(entries);
        LocalDate lastDate = getLastDate(entries);

        var analysis = months(firstDate, lastDate).stream().map(date -> analyzeByMonthUser(date, entries, splits))
                                                  .collect(Collectors.toList());

        computeCumulativeCredit(analysis);

        return analysis;
    }

    protected MonthlyUserAnalysis analyzeByMonthUser(LocalDate referenceMonth,
                                                     List<BoardEntryGroupByMonthUserCategory> entries,
                                                     List<BoardSplit> splits) {
        MonthlyUserAnalysis analysis = new MonthlyUserAnalysis();
        analysis.setYear(referenceMonth.getYear());
        analysis.setMonth(referenceMonth.getMonthValue());
        Map<Long, UserAmount> usersAmount = initializeUserAmounts(entries).stream()
                                                                          .collect(Collectors.toMap(UserAmount::getUserId, Function.identity()));
        entries.stream().filter(e -> hasSameYearMonth(e, referenceMonth))
               .forEach(e -> usersAmount.get(e.getUserId()).addActual(e.getAmount()));
        Map<Long, BoardSplit> targetSplits = getUserSplits(referenceMonth, getUserIds(entries), splits);

        computeExpected(usersAmount, targetSplits);

        analysis.setUsers(usersAmount.values().stream().collect(Collectors.toList()));
        return analysis;
    }

    protected void computeCumulativeCredit(List<MonthlyUserAnalysis> analysis) {
        List<Long> userIds = analysis.get(0).getUsers().stream().map(UserAmount::getUserId)
                                     .collect(Collectors.toList());

        userIds.forEach(userId -> computeCumulativeCredit(analysis, userId));
    }

    protected void computeCumulativeCredit(List<MonthlyUserAnalysis> analysis, long userId) {
        BigDecimal cumulatedCredit = BigDecimal.ZERO;
        for (MonthlyUserAnalysis entry : analysis) {
            UserAmount userEntry = entry.getUsers().stream().filter(u -> u.getUserId() == userId).findFirst()
                                        .orElseThrow(() -> new NullPointerException("Cannot find user " + userId));
            BigDecimal credit = userEntry.getActual().subtract(userEntry.getExpected());
            cumulatedCredit = cumulatedCredit.add(credit);
            userEntry.setCumulatedCredit(cumulatedCredit);
        }
    }

    protected void computeExpected(Map<Long, UserAmount> usersAmount, Map<Long, BoardSplit> targetSplits) {
        BigDecimal total = sum(usersAmount.values().stream().map(UserAmount::getActual));
        usersAmount.forEach((userId, userAmount) -> {
            userAmount.setExpected(expected(total, targetSplits.get(userId)));
        });
    }

    protected LocalDate date(BoardEntryGroupByMonthUserCategory e) {
        return LocalDate.of(e.getYear(), e.getMonth(), 1);
    }

    protected BigDecimal expected(BigDecimal total, BoardSplit split) {
        return total.multiply(split.getValue1(), MathContext.DECIMAL32);
    }

    protected LocalDate fromDate(BoardSplit split) {
        if (split.getFromYear() != null && split.getFromMonth() != null) {
            return LocalDate.of(split.getFromYear(), split.getFromMonth(), 1);
        }
        return LocalDate.MIN;
    }

    protected Stream<Long> getUserIds(List<BoardEntryGroupByMonthUserCategory> entries) {
        return entries.stream().map(BoardEntryGroupByMonthUserCategory::getUserId).distinct();
    }

    protected Map<Long, BoardSplit> getUserSplits(LocalDate referenceDate, Stream<Long> userIds,
                                                  List<BoardSplit> splits) {
        return userIds.collect(Collectors.toMap(Function.identity(), id -> getUserSplits(referenceDate, id, splits)));
    }

    protected BoardSplit getUserSplits(LocalDate referenceDate, Long userId, List<BoardSplit> splits) {
        List<BoardSplit> validSplits = splits.stream().filter(s -> isSameUser(s, userId))
                                             .filter(s -> isInRange(s, referenceDate)).collect(Collectors.toList());

        if (validSplits.isEmpty()) {
            throw new IllegalArgumentException("Cannot find a valid split for user=" + userId + ", date=" + referenceDate);
        }
        if (validSplits.size() == 1) {
            return validSplits.get(0);
        }

        List<BoardSplit> nonDefaults = validSplits.stream().filter(this::isNotDefaultSplit)
                                                  .collect(Collectors.toList());
        if (!nonDefaults.isEmpty()) {
            return nonDefaults.get(0);
        }
        if (nonDefaults.size() > 1) {
            throw new IllegalArgumentException("Found " + nonDefaults.size() + " splits for user=" + userId + ", " +
                    "date=" + referenceDate + ". Splits: " + nonDefaults);
        }
        List<BoardSplit> defaults = validSplits.stream().filter(this::isDefaultSplit).collect(Collectors.toList());
        if (defaults.size() == 1) {
            return defaults.get(0);
        }
        throw new IllegalArgumentException("Found " + defaults.size() + " default splits for user=" + userId + ", " + "date=" + referenceDate + ". Splits: " + defaults);
    }

    protected boolean hasSameYearMonth(BoardEntryGroupByMonthUserCategory entry, LocalDate date) {
        return date.getYear() == entry.getYear() && date.getMonthValue() == entry.getMonth();
    }

    protected List<UserAmount> initializeUserAmounts(List<BoardEntryGroupByMonthUserCategory> entries) {
        return getUserIds(entries).map(this::userAmount).collect(Collectors.toList());
    }

    protected boolean isDefaultSplit(BoardSplit split) {
        return split.getFromYear() == null && split.getFromMonth() == null && split.getToYear() == null && split.getToMonth() == null;
    }

    protected boolean isInRange(BoardSplit split, LocalDate referenceDate) {
        LocalDate from = fromDate(split);
        LocalDate to = toDate(split);
        return !from.isAfter(referenceDate) && !referenceDate.isAfter(to);
    }

    protected boolean isNotDefaultSplit(BoardSplit split) {
        return !isDefaultSplit(split);
    }

    protected boolean isSameUser(BoardSplit split, long userId) {
        return split.getUserId() == userId;
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

    protected BigDecimal sum(Stream<BigDecimal> stream) {
        return stream.reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    protected LocalDate toDate(BoardSplit split) {
        if (split.getToYear() != null && split.getToMonth() != null) {
            return LocalDate.of(split.getToYear(), split.getToMonth(), 1);
        }
        return LocalDate.MAX;
    }

    protected UserAmount userAmount(long userId) {
        UserAmount u = new UserAmount();
        u.setUserId(userId);
        u.setActual(BigDecimal.ZERO);
        return u;
    }

    private LocalDate getFirstDate(List<BoardEntryGroupByMonthUserCategory> entries) {
        return entries.stream().map(this::date).min(LocalDate::compareTo)
                      .orElseThrow(() -> new IllegalArgumentException("Cannot find a valid date"));
    }

    private LocalDate getLastDate(List<BoardEntryGroupByMonthUserCategory> entries) {
        return entries.stream().map(this::date).max(LocalDate::compareTo)
                      .orElseThrow(() -> new IllegalArgumentException("Cannot find a valid date"));
    }
}
