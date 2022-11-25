package pm.axe.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pm.axe.result.OperationResult;

/**
 * Validates idents.
 *
 * @since 3.0.4
 */
@Component
public class IdentValidator {

    /**
     * Marker, which indicates that provided ident is empty.
     */
    public static final String EMPTY_IDENT = "ERR_EMPTY_IDENT";
    /**
     * Marker, which indicates that provided ident is not valid.
     */
    public static final String MALFORMED_IDENT = "ERR_IDENT_DO_NOT_MATCH_PATTERN";

    /**
     * Performs ident validation against {@link IdentGenerator#VALID_IDENT_PATTERN}.
     *
     * @param ident string with ident to test
     * @return {@link OperationResult} with validate status: {@link OperationResult#OK} - if valid,
     * {@link OperationResult#MALFORMED_INPUT} with one of the above markers as message - if not valid.
     */
    public OperationResult validate(final String ident) {
        if (StringUtils.isBlank(ident)) {
           return OperationResult.malformedInput().withMessage(EMPTY_IDENT);
        }

        boolean isIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (isIdentValid) {
            return OperationResult.success();
        } else {
            return OperationResult.malformedInput().withMessage(MALFORMED_IDENT);
        }
    }
}
