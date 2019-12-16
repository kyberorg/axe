package eu.yals.utils;

import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.QueryParameters;
import eu.yals.constants.App;
import eu.yals.json.YalsErrorJson;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GuiUtils {

    private YalsErrorKeeper errorKeeper;

    public GuiUtils(YalsErrorKeeper errorKeeper) {
        this.errorKeeper = errorKeeper;
    }

    public YalsErrorJson getYalsErrorFromEvent(BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        if (queryParameters.getParameters().isEmpty()) return null;
        boolean errorIdKeyIsPresent = queryParameters.getParameters().containsKey(App.Params.ERROR_ID);
        if (!errorIdKeyIsPresent) return null;

        List<String> errorIdValues = queryParameters.getParameters().get(App.Params.ERROR_ID);
        boolean errorIdKeyHasSingleValue = errorIdValues.size() == 1;
        if (!errorIdKeyHasSingleValue) return null;

        String errorId = errorIdValues.get(0);
        Optional<YalsErrorJson> yalsErrorOptional = errorKeeper.get(errorId);
        return yalsErrorOptional.orElse(null);
    }

    public int parseStatusFromErrorParameter(ErrorParameter<? extends Exception> parameter, int defaultStatus) {
        int status;
        if (parameter != null && parameter.hasCustomMessage()) {
            String statusString = parameter.getCustomMessage();
            try {
                status = Integer.parseInt(statusString);
            } catch (Exception e) {
                status = defaultStatus;
            }
        } else {
            status = defaultStatus;
        }
        return status;
    }
}
