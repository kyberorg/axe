package eu.yals.result;

import lombok.Getter;

/**
 * Result of storing operation {@link Success} for positive result, {@link Fail} for negative.
 * {@link Fail} contains {@link Fail#errorMessage}
 *
 * @since 2.0
 */
public interface StoreResult {
    class Success implements StoreResult {
    }

    class Fail implements StoreResult {
        private final String errorMessage;

        public Fail(final String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    class DatabaseDown implements StoreResult {
        @Getter
        private final String errorMessage;
        @Getter
        private Throwable exception;

        public DatabaseDown() {
            this.errorMessage = "Database is DOWN";
        }

        public DatabaseDown withException(final Throwable e) {
            this.exception = e;
            return this;
        }
    }
}
