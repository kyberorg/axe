package ee.yals.result;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
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
