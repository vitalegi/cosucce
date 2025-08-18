package it.vitalegi.cosucce.budget.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class OptimisticLockException extends RuntimeException {
    UUID id;
    String expectedETag;
    String actualETag;

    public OptimisticLockException(UUID id, String expectedETag, String actualETag) {
        super("ETag mismatch on " + id + ", expected " + expectedETag + ", got " + actualETag);
        this.id = id;
        this.expectedETag = expectedETag;
        this.actualETag = actualETag;
    }
}
