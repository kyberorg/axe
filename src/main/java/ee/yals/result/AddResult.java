package ee.yals.result;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
public interface AddResult {
    class Success implements AddResult {
    }

    class Fail implements AddResult {
        private String errorMessage;

        public Fail(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
