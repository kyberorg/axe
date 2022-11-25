package pm.axe.ui.layouts;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pm.axe.constants.App;
import pm.axe.exception.error.AxeError;
import pm.axe.ui.pages.err.raw500.RawServerErrorPage;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ErrorUtils;

import java.util.Date;
import java.util.Optional;

/**
 * Basic layout for ServerError pages.
 *
 * @since 3.11
 */
@Slf4j
public class ServerErrorLayout extends AxeBaseLayout {
    public static final String TAG = "[" + ServerErrorLayout.class.getSimpleName() + "]";

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
     * Creates {@link RawServerErrorPage}.
     *
     * @param appUtils application utils
     */
    public ServerErrorLayout(final AppUtils appUtils) {
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

    /**
     * Extracts values from {@link AxeError} object and fills those values to fields accordingly.
     *
     * @param axeError non-empty error object to extract data from.
     */
    protected void fillUIWithValuesFromError(final AxeError axeError) {
        if (StringUtils.isNotBlank(axeError.getTimeStamp())) {
            when.setText(axeError.getTimeStamp());
        }
        if (StringUtils.isNotBlank(axeError.getMessageToUser())) {
            what.setText(axeError.getMessageToUser());
        }
        triggerPanelsBasedOnTextInside(userMessagePanel, timeStampPanel);

        if (appUtils.isDevelopmentModeActivated() || appUtils.hasDevHeader()) {
            if (StringUtils.isNotBlank(axeError.getTechMessage())) {
                String techMessage = formatTechMessage(axeError.getTechMessage());

                appUtils.pasteHtmlToComponent(techMessage, messageSpan);
            }
            if (axeError.getRawException() != null) {
                String traceMessage = ErrorUtils.stackTraceToString(axeError.getRawException());
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

}
