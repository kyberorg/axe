package pm.axe.redis.pubsub;

import pm.axe.session.AxeSession;

/**
 * Pub/Sub event types.
 *
 * @since 3.8
 */
public enum MessageEvent {
    /**
     * Event fired when {@link AxeSession} was updated by another instance.
     */
    AXE_SESSION_UPDATED,
    /**
     * Event fired when {@link AxeSession} was deleted by another instance.
     */
    AXE_SESSION_DELETED
}
