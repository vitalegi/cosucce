package it.vitalegi.budget.metrics;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
public class PerformanceAspect {

    @Around(value = "@target(Performance) && within(it.vitalegi..*)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object out = joinPoint.proceed();
            log.info("target={}, time={}, status=OK", getName((joinPoint)), time(start));
            return out;
        } catch (Throwable e) {
            Throwable root = root(e);
            log.info("target={}, time={}, status=KO, e={}, e_msg={}, root={}, root_msg={}", getName((joinPoint)), time(start), exName(e), e.getMessage(), exName(root), root.getMessage());
            throw e;
        }
    }

    protected String getName(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    protected Throwable root(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }

    protected String exName(Throwable e) {
        return e.getClass().getSimpleName();
    }

    protected long time(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
