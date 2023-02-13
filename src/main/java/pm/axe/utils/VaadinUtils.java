package pm.axe.utils;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.QueryParameters;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * Fit Layout into available windows. This helps to fit elements to small screens.
     *
     * @param layout {@link FlexComponent} layout to fit.
     */
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

    /**
     * Sets small spacing between elements in {@link HorizontalLayout}.
     *
     * @param layout target {@link HorizontalLayout}.
     */
    public static void setSmallSpacing(final HorizontalLayout layout) {
        layout.setSpacing(false);
        layout.getThemeList().add("spacing-s");
    }

    /**
     * Aligns given element at center.
     *
     * @param component element to center.
     */
    public static void setCentered(final HasStyle component) {
        component.addClassName("centered-element");
    }

    /**
     * Sets flex value. This is needed because Vaadin doesn't provide methods to change it.
     *
     * @param component target element to apply flex value to.
     * @param flexValue non-empty string with flex value.
     * @throws IllegalArgumentException when component or flex value is null.
     */
    public static void setFlex(final HasStyle component, final String flexValue) {
        if (component == null) throw new IllegalArgumentException("Component cannot be null");
        if (StringUtils.isBlank(flexValue)) throw new IllegalArgumentException("Flex Value cannot be blank");
        component.getStyle().set("flex", flexValue);
    }

    /**
     * Marks {@link Component} as invalid, sets error message and sets focus if {@link Component} is {@link Focusable}.
     *
     * @param component {@link Component} to check.
     * @param errorMessage non-empty string with error message. If error message is empty - no message will be set.
     * @throws IllegalArgumentException when component is NULL
     */
    public static void onInvalidInput(final HasValidation component, final String errorMessage) {
        if (component == null) throw new IllegalArgumentException("component cannot be null");
        component.setInvalid(true);
        if (StringUtils.isNotBlank(errorMessage)) {
            component.setErrorMessage(errorMessage);
        }
        if (component instanceof Focusable<?>) {
            ((Focusable<?>) component).focus();
        }
    }

    /**
     * Cleans input's value, validation state and error message.
     *
     * @param input {@link Component} to clean.
     */
    public static void cleanInput(final Component input) {
        if (input instanceof HasValue<?, ?>) {
            ((HasValue<?, ?>) input).clear();
        }
        if (input instanceof HasValidation) {
            ((HasValidation) input).setInvalid(false);
            ((HasValidation) input).setErrorMessage("");
        }
    }
}
