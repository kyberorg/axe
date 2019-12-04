package eu.yals.utils;

import eu.yals.json.YalsErrorJson;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Post for keeping error object
 */
@Component
public class YalsErrorKeeper {
    private Map<String, YalsErrorJson> repo = new ConcurrentHashMap<>();

    public String send(YalsErrorJson yalsError) {
        String errorId;
        do {
            errorId = UUID.randomUUID().toString();
        } while (repo.containsKey(errorId));

        repo.put(errorId, yalsError);
        return errorId;
    }

    public Optional<YalsErrorJson> get(String errorId) {
        return Optional.ofNullable(repo.get(errorId));
    }
}
