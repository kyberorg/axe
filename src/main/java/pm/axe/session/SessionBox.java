package pm.axe.session;

import pm.axe.constants.App;
import pm.axe.events.session.AxeSessionCreatedEvent;
import pm.axe.events.session.AxeSessionDestroyedEvent;
import pm.axe.redis.serializers.AxeSessionGsonRedisSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * In-memory session storage.
 *
 * @since 3.2
 */
@Slf4j
public final class SessionBox {
    private static final String TAG = "[" + SessionBox.class.getSimpleName() + "]";

    private static final Map<String, AxeSession> SESSION_STORAGE = new HashMap<>();
    private static final Map<String, AxeSession> PREVIOUS_VERSIONS = new HashMap<>();

    private static final AxeSessionGsonRedisSerializer SERIALIZER = new AxeSessionGsonRedisSerializer();

    private SessionBox() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Stores new session. It will be stored only if not existed previously.
     * Also fires {@link AxeSessionCreatedEvent}.
     *
     * @param axeSession non null session to store.
     */
    public static void storeSession(final AxeSession axeSession) {
        if (axeSession == null) return;
        SESSION_STORAGE.putIfAbsent(axeSession.getSessionId(), axeSession);
        EventBus.getDefault().post(AxeSessionCreatedEvent.createWith(axeSession));
    }

    /**
     * Checks if {@link SessionBox} has {@link AxeSession} with given ID or not.
     *
     * @param sessionId non-empty string with session id to check.
     * @return true if {@link AxeSession} with given ID is found, false - if not.
     */
    public static boolean hasSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return false;
        return SESSION_STORAGE.containsKey(sessionId);
    }

    /**
     * Gets Stored session by its ID.
     *
     * @param sessionId non-empty string with Session identifier.
     * @return {@link AxeSession} stored or {@code null} if not found or provided sessionId is blank.
     */
    public static AxeSession getSession(final String sessionId) {
        if (StringUtils.isBlank(sessionId)) return null;
        return SESSION_STORAGE.get(sessionId);
    }

    /**
     * Get all stored sessions.
     *
     * @return collection of currently stored {@link AxeSession}.
     */
    public static Collection<AxeSession> getAllSessions() {
        return SESSION_STORAGE.values();
    }

    /**
     * Updates stored session.
     *
     * @param axeSession session to update.
     */
    public static void updateSession(final AxeSession axeSession) {
        if (axeSession == null) return;
        SESSION_STORAGE.put(axeSession.getSessionId(), axeSession);
    }

    /**
     * Removes provided {@link AxeSession} from {@link SessionBox}.
     *
     * @param session stored {@link AxeSession} to remove.
     */
    public static void removeSession(final AxeSession session) {
        if (session == null) return;
        SESSION_STORAGE.remove(session.getSessionId());
        EventBus.getDefault().post(AxeSessionDestroyedEvent.createWith(session));
    }

    /**
     * Checks if {@link #PREVIOUS_VERSIONS} has given session.
     *
     * @param session session to search for.
     * @return true if {@link #PREVIOUS_VERSIONS} has given session, false if not.
     */
    static boolean hasPreviousVersion(final AxeSession session) {
        if (session == null) return false;
        return PREVIOUS_VERSIONS.containsKey(session.getSessionId());
    }

    /**
     * Gets previous version of {@link AxeSession} from {@link #PREVIOUS_VERSIONS} map.
     * It is good to use {@link #hasPreviousVersion(AxeSession)} before to avoid {@code null}
     *
     * @param session session, previous version of which we retrieve.
     * @return previous version of {@link AxeSession} or {@code null}
     * @throws IllegalArgumentException when session is null.
     * @see HashMap#get(Object)
     */
    static AxeSession getPreviousVersion(final AxeSession session) {
        if (session == null) throw new IllegalArgumentException("Session is null");
        return PREVIOUS_VERSIONS.get(session.getSessionId());
    }

    /**
     * Sets given session as previous, so next check can get it using {@link #getPreviousVersion(AxeSession)}.
     *
     * @param session valid {@link AxeSession}.
     * @return same session as it param to make it work with Stream API.
     */
    @SneakyThrows
    static AxeSession setAsPreviousVersion(final AxeSession session) {
        byte[] json = SERIALIZER.serialize(session);
        AxeSession prevSession = SERIALIZER.deserialize(json);
        if (prevSession != null) {
            PREVIOUS_VERSIONS.put(prevSession.getSessionId(), prevSession);
        }
        return session;
    }

    /**
     * Deletes session from {@link #PREVIOUS_VERSIONS} map.
     *
     * @param session session to delete.
     */
    static void deletePreviousVersion(final AxeSession session) {
        if (session == null) throw new IllegalArgumentException("Session is null");
        PREVIOUS_VERSIONS.remove(session.getSessionId());
    }

    /**
     * Prints Session diff.
     *
     * @param previous previous version of {@link AxeSession}.
     * @param current  current version of {@link AxeSession}.
     * @return current version of {@link AxeSession}.
     */
    static AxeSession logSessionsDiff(final AxeSession previous, final AxeSession current) {
        StringBuilder sb = new StringBuilder("Session difference detected");
        sb.append(App.NEW_LINE);
        sb.append("Session ").append("'").append(current.getSessionId()).append("' changed").append(App.NEW_LINE);
        sb.append("-----").append(App.NEW_LINE);

        Field[] fields = AxeSession.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    Object prevValue = field.get(previous);
                    Object currentValue = field.get(current);

                    boolean valuesAreDifferent;
                    if (currentValue == null) {
                        if (prevValue != null) {
                            sb.append(field.getName()).append(": ")
                                    .append(prevValue).append(" -> ")
                                    .append("null")
                                    .append(App.NEW_LINE);
                        }
                        valuesAreDifferent = false;
                    } else if (currentValue instanceof Date) {
                        valuesAreDifferent = false; //ignore this shit
                    } else {
                        valuesAreDifferent = !currentValue.equals(prevValue);
                    }
                    if (valuesAreDifferent) {
                        if (currentValue instanceof AxeSession.Flags) {
                            Field[] flags = AxeSession.Flags.class.getDeclaredFields();
                            for (Field flag : flags) {
                                if (flag.getName().equals("this$0")) break;
                                flag.setAccessible(true);
                                Object prevFlagValue = flag.get(previous.getFlags());
                                Object currentFlagValue = flag.get(current.getFlags());
                                boolean flagValuesAreDifferent = currentFlagValue != null
                                        && !currentFlagValue.equals(prevFlagValue);
                                if (flagValuesAreDifferent) {
                                    sb.append("Flags.").append(flag.getName()).append(": ")
                                            .append(prevFlagValue).append(" -> ")
                                            .append(currentFlagValue).append(App.NEW_LINE);
                                }
                            }
                        } else if (currentValue instanceof AxeSession.Settings) {
                            Field[] settings = AxeSession.Settings.class.getDeclaredFields();
                            for (Field setting : settings) {
                                if (setting.getName().equals("this$0")) break;
                                setting.setAccessible(true);
                                Object prevSetting = setting.get(previous.getSettings());
                                Object currentSetting = setting.get(current.getSettings());

                                boolean settingsAreDifferent = currentSetting != null
                                        && !currentSetting.equals(prevSetting);
                                if (settingsAreDifferent) {
                                    sb.append("Settings.").append(setting.getName()).append(": ")
                                            .append(prevSetting).append(" -> ")
                                            .append(currentSetting).append(App.NEW_LINE);
                                }
                            }
                        } else {
                            sb.append(field.getName()).append(": ")
                                    .append(prevValue).append(" -> ")
                                    .append(currentValue)
                                    .append(App.NEW_LINE);
                        }
                    }
                } catch (IllegalAccessException e) {
                    sb.append(field.getName()).append("No access").append(App.NEW_LINE);
                } catch (Exception e) {
                    log.warn("{} got exception while logging Sessions diff. {}", TAG, e);
                }
            }
        });
        log.info("{} {}", TAG, sb);
        return current;
    }
}
