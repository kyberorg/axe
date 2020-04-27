package eu.yals.utils;

import eu.yals.constants.App;
import eu.yals.exception.error.YalsError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static eu.yals.constants.App.NO_STATUS;

/**
 * Post for keeping error objects.
 *
 * @since 2.7
 */
@Slf4j
@Component
public class YalsErrorKeeper {
    private static final String TAG = "[" + YalsErrorKeeper.class.getSimpleName() + "]";

    private final Map<String, YalsError> repo = new ConcurrentHashMap<>();

    /**
     * Stores an error.
     *
     * @param yalsError error object to store
     * @return string with error id
     */
    public String send(final YalsError yalsError) {
        String errorId;
        do {
            errorId = UUID.randomUUID().toString();
        } while (repo.containsKey(errorId));

        yalsError.setId(errorId);

        repo.put(errorId, yalsError);

        logYalsError(yalsError);

        return errorId;
    }

    /**
     * Getting stored error object by its id.
     *
     * @param errorId id of searched error
     * @return {@link Optional} of found error
     */
    public Optional<YalsError> get(final String errorId) {
        return Optional.ofNullable(repo.get(errorId));
    }

    private void logYalsError(final YalsError yalsError) {
        StringBuilder logMessage = new StringBuilder("=== Error ===").append(App.NEW_LINE);
        logMessage.append("Error ID: ").append(yalsError.getId()).append(App.NEW_LINE);
        logMessage.append("TimeStamp: ").append(yalsError.getTimeStamp()).append(App.NEW_LINE);

        if (StringUtils.isNotBlank(yalsError.getTechMessage())) {
            logMessage.append("Tech message: ").append(yalsError.getTechMessage()).append(App.NEW_LINE);
        }

        if (yalsError.getHttpStatus() != NO_STATUS) {
            logMessage.append("Status: ").append(yalsError.getHttpStatus()).append(App.NEW_LINE);
        }
        if (yalsError.getRawException() != null) {
            if(log.isDebugEnabled()) {
                logMessage.append("Trace: ")
                        .append(ErrorUtils.stackTraceToString(yalsError.getRawException())).append(App.NEW_LINE);
            } else {
                logMessage.append("Exception: ")
                        .append(yalsError.getRawException().getMessage()).append(App.NEW_LINE);
            }
        }
        logMessage.append("=============");
        logMessage.trimToSize();
        log.error("{} {}", TAG, logMessage.toString());
    }
}
