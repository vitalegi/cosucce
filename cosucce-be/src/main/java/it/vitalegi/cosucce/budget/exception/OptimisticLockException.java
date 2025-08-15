package it.vitalegi.cosucce.budget.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class OptimisticLockException extends RuntimeException {
    UUID id;
    int expectedVersion;
    int actualVersion;

    public OptimisticLockException(UUID id, int expectedVersion, int actualVersion) {
        super("Version mismatch on " + id + ", expected " + expectedVersion + ", got " + actualVersion);
        this.id = id;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }
}
