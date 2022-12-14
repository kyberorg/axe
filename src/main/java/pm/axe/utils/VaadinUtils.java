package pm.axe.utils;

import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.QueryParameters;

import java.util.Optional;

/**
 * Vaadin-specific tools.
 */
public final class VaadinUtils {
    /**
     * Searches for given Query Parameter.
     *
     * @param paramName string with parameter name.
     * @param event {@link BeforeEvent} to get params from.
     * @return true if parameter exists, false is not.
     */
    public static boolean isParamPresent(final String paramName, final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().containsKey(paramName);
    }

    /**
     * Gets parameter's value.
     *
     * @param paramName string with parameter name.
     * @param event {@link BeforeEvent} to get params from.
     * @return {@link Optional} with param value or {@link Optional#empty()}.
     */
    public static Optional<String> getParamValue(final String paramName, final BeforeEvent event) {
        if (isParamPresent(paramName, event)) {
            QueryParameters queryParameters = event.getLocation().getQueryParameters();
            if (queryParameters.getParameters().get(paramName).size() > 0) {
                return Optional.of(queryParameters.getParameters().get(paramName).get(0));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
