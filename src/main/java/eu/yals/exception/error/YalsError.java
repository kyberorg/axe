package eu.yals.exception.error;

import eu.yals.ui.err.ServerErrorView;
import lombok.Data;

import java.util.Date;

/**
 * Useful data about happened error in application. Used for error reporting in {@link ServerErrorView}
 *
 * @since 2.7
 */
@Data
public class YalsError {
    private String id;
    private final String timeStamp = new Date().toString();
    private String messageToUser;
    private String techMessage;
    private int httpStatus = 500;
    private Throwable rawException;

    @Override
    public String toString() {
        return YalsError.class.getSimpleName() + ": " + getTechMessage();
    }
}
