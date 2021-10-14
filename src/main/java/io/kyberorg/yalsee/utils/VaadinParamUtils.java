package io.kyberorg.yalsee.utils;

import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.QueryParameters;

public final class VaadinParamUtils {
    public static boolean requestHasNoParams(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().isEmpty();
    }

    public static boolean isParamPresent(final String paramName, final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().containsKey(paramName);
    }

    public static String getParamValue(final String paramName, final BeforeEvent event) {
        if (isParamPresent(paramName, event)) {
            QueryParameters queryParameters = event.getLocation().getQueryParameters();
            if (queryParameters.getParameters().get(paramName).size() > 0) {
                return queryParameters.getParameters().get(paramName).get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
