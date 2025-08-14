package it.vitalegi.cosucce.budget.exception;

public class BudgetException extends RuntimeException {
    public BudgetException() {
    }

    public BudgetException(String message) {
        super(message);
    }

    public BudgetException(String message, Throwable cause) {
        super(message, cause);
    }

    public BudgetException(Throwable cause) {
        super(cause);
    }

    public BudgetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
