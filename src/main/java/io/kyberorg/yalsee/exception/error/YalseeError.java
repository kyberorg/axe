package io.kyberorg.yalsee.exception.error;

import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.ui.err.ServerErrorView;
import lombok.Data;

import java.util.Date;

/**
 * Useful data about happened error in application.
 * Used for error reporting in {@link ServerErrorView}.
 *
 * @since 2.7
 */
@Data
public class YalseeError {
    private String id;
    private final String timeStamp = new Date().toString();
    private String messageToUser;
    private String techMessage;
    private int httpStatus = HttpCode.STATUS_500;
    private Throwable rawException;
    private String path;

    @Override
    public String toString() {
        return YalseeError.class.getSimpleName() + ": " + getTechMessage();
    }
}
