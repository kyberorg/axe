package ee.yals.result;

/**
 * Result of storing operation
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public interface StoreResult {
    class Success implements StoreResult {
    }

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
