package eu.yals.exception.error;

import lombok.Data;

import java.util.Date;

import static eu.yals.constants.HttpCode.STATUS_500;

/**
 * Useful data about happened error in application.
 * Used for error reporting in {@link eu.yals.ui.err.ServerErrorView}.
 *
 * @since 2.7
 */
@Data
public class YalsError {
    private String id;
    private final String timeStamp = new Date().toString();
    private String messageToUser;
    private String techMessage;
    private int httpStatus = STATUS_500;
    private Throwable rawException;

    @Override
    public String toString() {
        return YalsError.class.getSimpleName() + ": " + getTechMessage();
    }
}
