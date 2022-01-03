package io.kyberorg.yalsee.ui.err;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.controllers.YalseeErrorController;
import io.kyberorg.yalsee.exception.error.YalseeError;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@SpringComponent
@UIScope
@PageTitle("Yalsee: Error 500")
@Route(value = Endpoint.UI.RAW_ERROR_PAGE_500)
@CssImport("./css/error_views.css")
public class RawServerErrorView extends YalseeLayout implements HasUrlParameter<String> {
    public static final String TAG = "[" + RawServerErrorView.class.getSimpleName() + "]";

    private final ErrorUtils errorUtils;
    private final AppUtils appUtils;

    private final H1 title = new H1();
    private final Span subTitle = new Span();

    private final Image image = new Image();

    private final Accordion techInfo = new Accordion();
    private AccordionPanel userMessagePanel;
    private AccordionPanel timeStampPanel;
    private AccordionPanel techMessagePanel;
    private AccordionPanel tracePanel;

    private final Span when = new Span();
    private final Span what = new Span();
    private final Span messageSpan = new Span();
    private final Span traceSpan = new Span();

    /**
     * Creates {@link RawServerErrorView}.
     *
     * @param errorUtils error utils for actions with errors
     * @param appUtils   application utils
     */
    public RawServerErrorView(final ErrorUtils errorUtils, final AppUtils appUtils) {
        this.errorUtils = errorUtils;
        this.appUtils = appUtils;

        init();
        applyStyle();
        add(title, subTitle, image, techInfo);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        title.setText("Something is technically wrong");
        subTitle.setText("Sorry, it is not you, it is us. We will take a look into...");

        image.setSrc("images/500.jpg");
        image.setAlt("Error 500 Image");
        image.addClickListener(this::onImageClick);

        techInfo.setVisible(false);
        initTechInfo();
    }

    private void applyStyle() {
        image.addClassName("error-image");
        image.addClassName("centered-image");

        techInfo.addClassName("error-image");
        techInfo.addClassName("centered-image");
    }

    private void onImageClick(final ClickEvent<Image> imageClickEvent) {
        log.trace("{} Image click event. Is from client? {}", TAG, imageClickEvent.isFromClient());
        image.setVisible(false);
        techInfo.setVisible(true);
    }

    private void initTechInfo() {
        when.setText(new Date().toString());
        what.setText("There was an unexpected error");

        messageSpan.setText("");
        traceSpan.setText("");
        traceSpan.setEnabled(false);

        userMessagePanel = techInfo.add("What happened?", what);
        timeStampPanel = techInfo.add("When happened?", when);
        triggerPanelsBasedOnTextInside(userMessagePanel, timeStampPanel);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            techMessagePanel = techInfo.add("Tech message", messageSpan);
            tracePanel = techInfo.add("Trace", traceSpan);
            triggerPanelsBasedOnTextInside(techMessagePanel, tracePanel);
        }
    }

    private void fillUIWithValuesFromError(final YalseeError yalseeError) {
        if (StringUtils.isNotBlank(yalseeError.getTimeStamp())) {
            when.setText(yalseeError.getTimeStamp());
        }
        if (StringUtils.isNotBlank(yalseeError.getMessageToUser())) {
            what.setText(yalseeError.getMessageToUser());
        }
        triggerPanelsBasedOnTextInside(userMessagePanel, timeStampPanel);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            if (StringUtils.isNotBlank(yalseeError.getTechMessage())) {
                String techMessage = formatTechMessage(yalseeError.getTechMessage());

                appUtils.pasteHtmlToComponent(techMessage, messageSpan);
            }
            if (yalseeError.getRawException() != null) {
                String traceMessage = ErrorUtils.stackTraceToString(yalseeError.getRawException());
                String trace = formatTrace(traceMessage);

                appUtils.pasteHtmlToComponent(trace, traceSpan);
            }
            triggerPanelsBasedOnTextInside(techMessagePanel, tracePanel);
        }
    }

    /**
     * Disables given accordion panels if they are empty, enables otherwise.
     *
     * @param panels initialised accordion panels
     */
    private void triggerPanelsBasedOnTextInside(final AccordionPanel... panels) {
        for (AccordionPanel panel : panels) {
            Optional<Component> elementWithText = panel.getContent().findFirst();
            if (elementWithText.isPresent()) {
                Span spanWithText = (Span) elementWithText.get();
                boolean ifSpanHasText = StringUtils.isNotBlank(spanWithText.getText())
                        || StringUtils.isNotBlank(spanWithText.getElement().getProperty(AppUtils.HTML_MODE));

                panel.setEnabled(ifSpanHasText);
            } else {
                panel.setEnabled(false);
            }
        }
    }

    private String formatTechMessage(final String techMessage) {
        return techMessage
                .replaceAll(App.NEW_LINE, App.WEB_NEW_LINE)
                .replaceAll(";", App.WEB_NEW_LINE)
                .replaceAll("nested exception is", "&emsp;->");
    }

    private String formatTrace(final String traceMessage) {
        return traceMessage.replaceAll(App.NEW_LINE, App.WEB_NEW_LINE)
                .replaceAll("at ", "&emsp;&emsp;at ");
    }

    /**
     * EntryPoint from {@link YalseeErrorController}.
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        YalseeError yalseeError = errorUtils.getYalseeErrorFromEvent(event);
        if (!Objects.isNull(yalseeError)) {
            fillUIWithValuesFromError(yalseeError);
        }
    }
}
