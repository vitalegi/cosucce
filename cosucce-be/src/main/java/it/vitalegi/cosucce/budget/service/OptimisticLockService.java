package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OptimisticLockService {

    public void checkLock(UUID entryId, String expectedETag, String actualETag) {
        if (!expectedETag.equalsIgnoreCase(actualETag)) {
            throw new OptimisticLockException(entryId, expectedETag, actualETag);
        }
    }
}
