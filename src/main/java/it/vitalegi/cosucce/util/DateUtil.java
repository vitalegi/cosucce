package it.vitalegi.cosucce.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static long daysBetween(LocalDate date1, LocalDate date2) {
        if (date1.compareTo(date2) <= 0) {
            return ChronoUnit.DAYS.between(date1, date2);
        }
        return ChronoUnit.DAYS.between(date2, date1);
    }
}
