package pm.axe.internal;

import com.vaadin.flow.component.tabs.Tab;
import pm.axe.db.models.User;

/**
 * This interface determines, that {@link Tab} has {@link #tabInit(User)} method.
 */
public interface HasTabInit {
    /**
     * Method, that should be called at initialization phase.
     *
     * @param user bound {@link User}. Usually stored within current session.
     */
    void tabInit(User user);
}
