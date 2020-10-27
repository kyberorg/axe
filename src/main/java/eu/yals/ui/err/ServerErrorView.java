package eu.yals.ui.err;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.exception.GeneralServerException;
import eu.yals.exception.NeedForLoopException;
import eu.yals.exception.error.YalsError;
import eu.yals.ui.AppView;
import eu.yals.utils.AppUtils;
import eu.yals.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static eu.yals.constants.HttpCode.*;
import static eu.yals.utils.AppUtils.HTML_MODE;

@Slf4j
@SpringComponent
@UIScope
@PageTitle("Yals: Error 500")
@Route(value = Endpoint.UI.ERROR_PAGE_500, layout = AppView.class)
public class ServerErrorView extends VerticalLayout implements HasErrorParameter<GeneralServerException>,
        HasUrlParameter<String> {
    public static final String TAG = "[" + ServerErrorView.class.getSimpleName() + "]";

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
     * Creates {@link ServerErrorView}.
     *
     * @param errorUtils error utils for actions with errors
     * @param appUtils   application utils
     */
    public ServerErrorView(final ErrorUtils errorUtils, final AppUtils appUtils) {
        this.errorUtils = errorUtils;
        this.appUtils = appUtils;

        init();
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

    private void fillUIWithValuesFromError(final YalsError yalsError) {
        if (StringUtils.isNotBlank(yalsError.getTimeStamp())) {
            when.setText(yalsError.getTimeStamp());
        }
        if (StringUtils.isNotBlank(yalsError.getMessageToUser())) {
            what.setText(yalsError.getMessageToUser());
        }
        triggerPanelsBasedOnTextInside(userMessagePanel, timeStampPanel);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            if (StringUtils.isNotBlank(yalsError.getTechMessage())) {
                String techMessage = formatTechMessage(yalsError.getTechMessage());

                appUtils.pasteHtmlToComponent(techMessage, messageSpan);
            }
            if (yalsError.getRawException() != null) {
                String traceMessage = ErrorUtils.stackTraceToString(yalsError.getRawException());
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
                        || StringUtils.isNotBlank(spanWithText.getElement().getProperty(HTML_MODE));

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
     * EntryPoint from {@link eu.yals.controllers.YalsErrorController}.
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        YalsError yalsError = errorUtils.getYalsErrorFromEvent(event);
        if (Objects.isNull(yalsError)) {
            event.rerouteToError(NeedForLoopException.class, Integer.toString(STATUS_500));
            return;
        }

        switch (yalsError.getHttpStatus()) {
            case STATUS_404:
                event.rerouteToError(NotFoundException.class);
                return;
            case STATUS_503:
                event.rerouteToError(CannotCreateTransactionException.class);
                return;
            default:
                fillUIWithValuesFromError(yalsError);
                event.rerouteToError(NeedForLoopException.class, Integer.toString(yalsError.getHttpStatus()));
                break;
        }
    }

    /**
     * This method sets HTTP Code, based on payload, if no payload - status is 500.
     *
     * @param event     same event as {@link #setParameter(BeforeEvent, String)}
     * @param parameter payload with status as String
     * @return http status
     */
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<GeneralServerException> parameter) {
        return errorUtils.parseStatusFromErrorParameter(parameter, STATUS_500);
    }
}
