package pm.axe.utils;

import pm.axe.ui.MainView;
import org.springframework.stereotype.Component;

/**
 * Detects redirects loops caused by exceptions at {@link MainView}.
 *
 * @since 3.8
 */
@Component
public class RedirectLoopDetector {
    private int redirects = 0;

    /**
     * Updates redirect counter. Should be placed just before actual redirect.
     */
    public void updateCounter() {
        redirects++;
    }

    /**
     * Read {@link #redirects} counter and reports if there is redirect loop detected.
     *
     * @return true - if there are more than one redirect, false - if not.
     */
    public boolean isLoopDetected() {
        return redirects > 1;
    }
}
