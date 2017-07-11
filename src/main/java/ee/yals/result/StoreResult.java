package ee.yals.result;

/**
 * Result of storing operation {@link Success} for positive result, {@link Fail} for negative.
 * {@link Fail} contains {@link Fail#errorMessage}
 *
 * @since 2.0
 */
public interface StoreResult {
    class Success implements StoreResult {}

    class Fail implements StoreResult {
        private String errorMessage;

        public Fail(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
