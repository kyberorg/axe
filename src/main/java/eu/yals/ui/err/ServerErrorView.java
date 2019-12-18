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
import eu.yals.controllers.YalsErrorController;
import eu.yals.exception.GeneralServerException;
import eu.yals.exception.error.YalsError;
import eu.yals.ui.AppView;
import eu.yals.utils.AppUtils;
import eu.yals.utils.ErrorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@SpringComponent
@UIScope
@PageTitle("Yals: Error 500")
@Route(value = Endpoint.UI.ERROR_PAGE_500, layout = AppView.class)
public class ServerErrorView extends VerticalLayout implements HasErrorParameter<GeneralServerException>, HasUrlParameter<String> {
    private static final String HTML_MODE = "innerHTML";
    private ErrorUtils errorUtils;

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
    private final Span message = new Span();
    private final Span trace = new Span();

    public ServerErrorView(ErrorUtils errorUtils) {
        this.errorUtils = errorUtils;

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

    private void onImageClick(ClickEvent<Image> imageClickEvent) {
        image.setVisible(false);
        techInfo.setVisible(true);
    }

    private void initTechInfo() {
        when.setText(new Date().toString());
        what.setText("There was an unexpected error");

        //TODO only in DevMode and disable if empty
        message.setText("");
        trace.setText("");
        trace.setEnabled(false);

        userMessagePanel = techInfo.add("What happened?", what);
        timeStampPanel = techInfo.add("When happened?", when);

        //TODO only in DevMode and disable if empty
        techMessagePanel = techInfo.add("Tech message", message);
        tracePanel = techInfo.add("Trace", trace);
        //TODO else "How can I help" section

        triggerPanelsByTextInside(userMessagePanel, timeStampPanel, techMessagePanel, tracePanel);
    }

    private void triggerPanelsByTextInside(AccordionPanel... panels) {
        for (AccordionPanel panel : panels) {
            Optional<Component> elementWithText = panel.getContent().findFirst();
            if (elementWithText.isPresent()) {
                Span spanWithText = (Span) elementWithText.get();
                boolean ifSpanHasText = StringUtils.isNotBlank(spanWithText.getText()) ||
                        StringUtils.isNotBlank(spanWithText.getElement().getProperty(HTML_MODE));

                panel.setEnabled(ifSpanHasText);
            } else {
                panel.setEnabled(false);
            }
        }
    }

    /**
     * EntryPoint from {@link YalsErrorController}
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        YalsError yalsError = errorUtils.getYalsErrorFromEvent(event);
        if (Objects.isNull(yalsError)) {
            event.rerouteToError(GeneralServerException.class, Integer.toString(500));
            return;
        }

        switch (yalsError.getHttpStatus()) {
            case 404:
                event.rerouteToError(NotFoundException.class);
                return;
            case 503:
                event.rerouteToError(CannotCreateTransactionException.class);
                return;
            default:
                fillUIWithValuesFromError(yalsError);
                event.rerouteToError(GeneralServerException.class, Integer.toString(yalsError.getHttpStatus()));
        }
    }

    /**
     * This method sets HTTP Code, based on payload, if no payload - status is 500
     *
     * @param event     same event as {@link #setParameter(BeforeEvent, String)}
     * @param parameter payload with status as String
     * @return http status
     */
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<GeneralServerException> parameter) {
        return errorUtils.parseStatusFromErrorParameter(parameter, 500);
    }

    private void fillUIWithValuesFromError(YalsError yalsError) {
        if (StringUtils.isNotBlank(yalsError.getTimeStamp())) {
            when.setText(yalsError.getTimeStamp());
        }
        if (StringUtils.isNotBlank(yalsError.getMessageToUser())) {
            what.setText(yalsError.getMessageToUser());
        }

        //TODO only in DevMode or with Developer Header
        if (StringUtils.isNotBlank(yalsError.getTechMessage())) {
            String techMessage = yalsError.getTechMessage();
            String techMessageForWeb = techMessage
                    .replaceAll(App.NEW_LINE, App.WEB_NEW_LINE)
                    .replaceAll(";", App.WEB_NEW_LINE)
                    .replaceAll("nested exception is", "&emsp;->");

            message.getElement().setProperty(HTML_MODE, techMessageForWeb);
        }
        if (yalsError.getRawException() != null) {
            String traceMessage = AppUtils.stackTraceToString(yalsError.getRawException());
            String traceForWeb = traceMessage.replaceAll(App.NEW_LINE, App.WEB_NEW_LINE)
                    .replaceAll("at ", "&emsp;&emsp;at ");

            trace.getElement().setProperty(HTML_MODE, traceForWeb);
        }

        triggerPanelsByTextInside(userMessagePanel, timeStampPanel, techMessagePanel, tracePanel);
    }
}
