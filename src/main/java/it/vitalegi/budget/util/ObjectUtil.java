package it.vitalegi.budget.util;

import org.springframework.beans.BeanUtils;

public class ObjectUtil {
    public static <S, D> D copy(S source, D dest) {
        BeanUtils.copyProperties(source, dest);
        return dest;
    }
}
