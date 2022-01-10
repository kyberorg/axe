package io.kyberorg.yalsee.redis.pubsub;

import io.kyberorg.yalsee.session.YalseeSession;

/**
 * Pub/Sub event types.
 *
 * @since 3.8
 */
public enum MessageEvent {
    /**
     * Event fired when {@link YalseeSession} was updated by another instance.
     */
    YALSEE_SESSION_UPDATED,
    /**
     * Event fired when {@link YalseeSession} was deleted by another instance.
     */
    YALSEE_SESSION_DELETED
}
