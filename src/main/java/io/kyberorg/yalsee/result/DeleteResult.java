package io.kyberorg.yalsee.result;

import lombok.Getter;

/**
 * Result of delete link operation. {@link Success} for positive result, {@link NotFound} for negative.
 *
 * @since 2.0
 */
public interface DeleteResult {
    class Success implements DeleteResult { }

    class NotFound implements DeleteResult {
        @Getter
        private final String errorMessage = "Nothing was found";
    }

    class Fail implements DeleteResult {
        @Getter
        private final String errorMessage = "Unknown Database Error";
        @Getter
        private Throwable exception;

        public Fail withException(final Throwable e) {
            this.exception = e;
            return this;
        }
    }

    class DatabaseDown implements DeleteResult {
        @Getter
        private final String errorMessage = "Database is DOWN";
        @Getter
        private Throwable exception;

        public DatabaseDown withException(final Throwable e) {
            this.exception = e;
            return this;
        }
    }
}
