package io.kyberorg.yalsee.core;

import io.kyberorg.yalsee.result.OperationResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class IdentValidator {

    public static final String EMPTY_IDENT = "ERR_EMPTY_IDENT";
    public static final String MALFORMED_IDENT = "ERR_IDENT_DO_NOT_MATCH_PATTERN";

    public OperationResult validate(String ident) {
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
