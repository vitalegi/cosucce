package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OptimisticLockService {

    public void checkLock(UUID entryId, int expectedVersion, int actualVersion) {
        if (expectedVersion != actualVersion) {
            throw new OptimisticLockException(entryId, expectedVersion, actualVersion);
        }
    }
}
