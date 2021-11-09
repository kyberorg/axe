package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.exception.error.YalseeError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
public class YalseeErrorKeeper {
    private static final String TAG = "[" + YalseeErrorKeeper.class.getSimpleName() + "]";

    private final Map<String, YalseeError> repo = new ConcurrentHashMap<>();

    /**
     * Stores an error.
     *
     * @param yalseeError error object to store
     * @return string with error id
     */
    public String send(final YalseeError yalseeError) {
        String errorId;
        do {
            errorId = UUID.randomUUID().toString();
        } while (repo.containsKey(errorId));

        yalseeError.setId(errorId);

        repo.put(errorId, yalseeError);

        logYalseeError(yalseeError);

        return errorId;
    }

    /**
     * Getting stored error object by its id.
     *
     * @param errorId id of searched error
     * @return {@link Optional} of found error
     */
    public Optional<YalseeError> get(final String errorId) {
        return Optional.ofNullable(repo.get(errorId));
    }

    private void logYalseeError(final YalseeError yalseeError) {
        StringBuilder logMessage = new StringBuilder("=== Error ===").append(App.NEW_LINE);
        logMessage.append("Error ID: ").append(yalseeError.getId()).append(App.NEW_LINE);
        logMessage.append("TimeStamp: ").append(yalseeError.getTimeStamp()).append(App.NEW_LINE);

        if (StringUtils.isNotBlank(yalseeError.getTechMessage())) {
            logMessage.append("Tech message: ").append(yalseeError.getTechMessage()).append(App.NEW_LINE);
        }

        if (yalseeError.getHttpStatus() != App.NO_STATUS) {
            logMessage.append("Status: ").append(yalseeError.getHttpStatus()).append(App.NEW_LINE);
        }
        if (yalseeError.getRawException() != null) {
            if (log.isDebugEnabled()) {
                logMessage.append("Trace: ")
                        .append(ErrorUtils.stackTraceToString(yalseeError.getRawException())).append(App.NEW_LINE);
            } else {
                logMessage.append("Exception: ")
                        .append(yalseeError.getRawException().getMessage()).append(App.NEW_LINE);
            }
        }
        logMessage.append("=============");
        logMessage.trimToSize();
        log.error("{} {}", TAG, logMessage);
    }
}
