package pm.axe.utils;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.QueryParameters;

import java.util.Optional;

/**
 * Vaadin-specific tools.
 */
public final class VaadinUtils {
    private VaadinUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
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

    public static void fitLayoutInWindow(final FlexComponent layout) {
        layout.addClassName("fit-in-window");
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
    }

    /**
     * For those elements, where {@link #fitLayoutInWindow(FlexComponent)} is not fit.
     *
     * @param layout {@link FlexComponent} that should be wrapped.
     */
    public static void setWrap(final FlexComponent layout) {
        layout.getStyle().set("flex-wrap", "wrap");
    }

    public static void setSmallSpacing(final HorizontalLayout layout) {
        layout.setSpacing(false);
        layout.getThemeList().add("spacing-s");
    }

    public static void setCentered(final HasStyle component) {
        component.addClassName("centered-element");
    }

    public static void setFlex(final HasStyle component, final String flexValue) {
        component.getStyle().set("flex", flexValue);
    }
}
