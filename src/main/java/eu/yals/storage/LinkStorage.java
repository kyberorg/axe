package eu.yals.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Local storage implementation, used as fallback, when DB is absent. Same as in-memory DB.
 *
 * @since 2.0
 */
@Component
public class LinkStorage {

    private static Map<String, String> storage = new HashMap<>();

    public static final String LINK_NOT_FOUND = "LinkNotFound";

    public void save(String ident, String link) {
        storage.put(ident, link);
    }

    public String find(String ident) {
        return storage.getOrDefault(ident, LINK_NOT_FOUND);
    }

}
