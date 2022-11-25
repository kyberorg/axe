package pm.axe.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pm.axe.constants.App;
import pm.axe.exception.error.AxeError;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Post for keeping error objects.
 *
 * @since 2.7
 */
@Slf4j
@Component
public class AxeErrorKeeper {
    private static final String TAG = "[" + AxeErrorKeeper.class.getSimpleName() + "]";

    private final Map<String, AxeError> repo = new ConcurrentHashMap<>();

    /**
     * Stores an error.
     *
     * @param axeError error object to store
     * @return string with error id
     */
    public String send(final AxeError axeError) {
        String errorId;
        do {
            errorId = UUID.randomUUID().toString();
        } while (repo.containsKey(errorId));

        axeError.setId(errorId);

        repo.put(errorId, axeError);

        logAxeError(axeError);

        return errorId;
    }

    /**
     * Getting stored error object by its id.
     *
     * @param errorId id of searched error
     * @return {@link Optional} of found error
     */
    public Optional<AxeError> get(final String errorId) {
        return Optional.ofNullable(repo.get(errorId));
    }

    private void logAxeError(final AxeError axeError) {
        StringBuilder logMessage = new StringBuilder("=== Error ===").append(App.NEW_LINE);
        logMessage.append("Error ID: ").append(axeError.getId()).append(App.NEW_LINE);
        logMessage.append("TimeStamp: ").append(axeError.getTimeStamp()).append(App.NEW_LINE);

        if (StringUtils.isNotBlank(axeError.getTechMessage())) {
            logMessage.append("Tech message: ").append(axeError.getTechMessage()).append(App.NEW_LINE);
        }

        if (axeError.getHttpStatus() != App.NO_STATUS) {
            logMessage.append("Status: ").append(axeError.getHttpStatus()).append(App.NEW_LINE);
        }
        if (axeError.getRawException() != null) {
            if (log.isDebugEnabled()) {
                logMessage.append("Trace: ")
                        .append(ErrorUtils.stackTraceToString(axeError.getRawException())).append(App.NEW_LINE);
            } else {
                logMessage.append("Exception: ")
                        .append(axeError.getRawException().getMessage()).append(App.NEW_LINE);
            }
        }
        logMessage.append("=============");
        logMessage.trimToSize();
        log.error("{} {}", TAG, logMessage);
    }
}
