package ee.yals.result;

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

}
