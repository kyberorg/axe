package pm.axe.exception.error;

import pm.axe.constants.HttpCode;
import pm.axe.ui.pages.err.err500.ServerErrorPage;
import lombok.Data;

import java.util.Date;

/**
 * Useful data about happened error in application.
 * Used for error reporting in {@link ServerErrorPage}.
 *
 * @since 2.7
 */
@Data
public class AxeError {
    private String id;
    private final String timeStamp = new Date().toString();
    private String messageToUser;
    private String techMessage;
    private int httpStatus = HttpCode.SERVER_ERROR;
    private Throwable rawException;
    private String path;

    @Override
    public String toString() {
        return AxeError.class.getSimpleName() + ": " + getTechMessage();
    }
}
