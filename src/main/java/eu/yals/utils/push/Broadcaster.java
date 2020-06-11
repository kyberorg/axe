package eu.yals.utils.push;

import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Push streamer.
 *
 * @since 2.7
 */
@Slf4j
public final class Broadcaster {
    private static final String TAG = "[" + Broadcaster.class.getSimpleName() + "]";
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private static final LinkedList<Consumer<String>> LISTENERS = new LinkedList<>();

    /**
     * Register as listener.
     *
     * @param listener message receiver
     * @return {@link Registration} object
     */
    public static synchronized Registration register(
            final Consumer<String> listener) {
        LISTENERS.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                LISTENERS.remove(listener);
            }
        };
    }

    /**
     * Stream message.
     *
     * @param message string with message to stream
     */
    public static synchronized void broadcast(final String message) {
        log.debug("{} Broadcasting message: {}", TAG, message);
        for (Consumer<String> listener : LISTENERS) {
            EXECUTOR.execute(() -> listener.accept(message));
        }
    }

    private Broadcaster() {
        throw new UnsupportedOperationException("Utility class");
    }
}
