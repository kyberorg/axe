package ee.yals.result;

import lombok.Getter;

/**
 * Result of get link operation. {@link Success} for positive result, {@link NotFound} for negative.
 * {@link Success} contains {@link Success#link}
 *
 * @since 2.0
 */
public interface GetResult {
    class Success implements GetResult {
        private String link;

        public Success(String link) {
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }

    class NotFound implements GetResult {
        private String errorMessage;

        public NotFound() {
            this.errorMessage = "Nothing was found by this ident";
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    class Fail implements GetResult {
        @Getter
        private final String errorMessage;
        @Getter
        private Throwable exception;

        public Fail() {
            this.errorMessage = "Unknown Database Error";
        }

        public Fail withException(Throwable e) {
            this.exception = e;
            return this;
        }
    }

    class DatabaseDown implements GetResult {
        @Getter
        private final String errorMessage;
        @Getter
        private Throwable exception;

        public DatabaseDown() {
            this.errorMessage = "Database is DOWN";
        }

        public DatabaseDown withException(Throwable e) {
            this.exception = e;
            return this;
        }
    }
}
