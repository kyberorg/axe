package io.kyberorg.yalsee.ui.pages.home;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.events.link.LinkDeletedEvent;
import io.kyberorg.yalsee.events.link.LinkSavedEvent;
import io.kyberorg.yalsee.exception.error.YalseeErrorBuilder;
import io.kyberorg.yalsee.internal.LinkServiceInput;
import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.QRCodeService;
import io.kyberorg.yalsee.services.overall.OverallService;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ClipboardUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import io.kyberorg.yalsee.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@RequiredArgsConstructor
@Slf4j
@SpringComponent
@UIScope
@CssImport("./css/common_styles.css")
@CssImport("./css/home_view.css")
@Route(value = Endpoint.UI.HOME_PAGE, layout = MainView.class)
@PageTitle("Yalsee - the link shortener")
public class HomePage extends HorizontalLayout implements BeforeEnterObserver {
    private static final String TAG = "[" + HomePage.class.getSimpleName() + "]";

    private final Div leftDiv = new Div();
    private final VerticalLayout centralLayout = new VerticalLayout();
    private final Div rightDiv = new Div();

    private final Component mainArea = mainArea();
    private final Component overallArea = overallArea();
    private final Component resultArea = resultArea();
    private final Component qrCodeArea = qrCodeArea();
    private final Component myLinksNoteArea = myLinksNoteArea();

    private final OverallService overallService;
    private final LinkService linkService;
    private final QRCodeService qrCodeService;
    private final AppUtils appUtils;
    private final ErrorUtils errorUtils;
    private UI ui;

    private Span titleLongPart;
    private TextField input;
    private RadioButtonGroup<String> protocolSelector;
    private Accordion descriptionAccordion;
    private TextField descriptionInput;
    private Button submitButton;
    private Anchor shortLink;
    private Image qrCode;

    private Span linkCounter;

    private Notification errorNotification;

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        this.setId(IDs.VIEW_ID);

        centralLayout.removeAll();
        centralLayout.add(mainArea, overallArea, resultArea, myLinksNoteArea, qrCodeArea);

        removeAll();
        add(leftDiv, centralLayout, rightDiv);
    }

    private void applyStyle() {
        leftDiv.addClassName("responsive-div");
        centralLayout.addClassName("responsive-home-page-center");
        rightDiv.addClassName("responsive-div");

        titleLongPart.addClassName("title-long-text");
    }

    private void applyLoadState() {
        long linksStored = overallService.numberOfStoredLinks();
        linkCounter.setText(Long.toString(linksStored));

        input.setAutofocus(true);
        protocolSelector.setVisible(false);
        descriptionAccordion.close();
        submitButton.setEnabled(true);

        mainArea.setVisible(true);
        overallArea.setVisible(true);
        resultArea.setVisible(false);
        qrCodeArea.setVisible(false);
        myLinksNoteArea.setVisible(false);
    }

    private VerticalLayout mainArea() {
        Span titlePartOne = new Span("Make your ");
        titleLongPart = new Span("long ");
        Span titleLastPart = new Span("links short");

        H2 title = new H2(titlePartOne, titleLongPart, titleLastPart);
        title.setId(IDs.TITLE);
        title.addClassName("compact-title");

        input = new TextField("Your very long URL here:");
        input.setId(IDs.INPUT);
        input.setPlaceholder("https://mysuperlongurlhere.tld");
        input.setWidthFull();
        input.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        input.addValueChangeListener(this::onInputChanged);

        protocolSelector = new RadioButtonGroup<>();
        protocolSelector.setId(IDs.PROTOCOL_SELECTOR);
        protocolSelector.setLabel("Protocol");
        protocolSelector.setItems("https://", "http://", "ftp://");

        descriptionAccordion = new Accordion();
        descriptionAccordion.setId(IDs.DESCRIPTION_ACCORDION);
        descriptionAccordion.setWidthFull();

        descriptionInput = new TextField();
        descriptionInput.setId(IDs.DESCRIPTION_INPUT);
        descriptionInput.setPlaceholder("what is link about...");
        descriptionInput.setWidthFull();

        Span publicAccessBanner =
                new Span("Note: all links considered as public and can be used by anyone");
        publicAccessBanner.setId(IDs.BANNER);

        submitButton = new Button("Shorten it!");
        submitButton.setId(IDs.SUBMIT_BUTTON);
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(this::onSaveLink);
        submitButton.addClickShortcut(Key.ENTER);

        descriptionAccordion.add("Link Description (optional)", descriptionInput);

        VerticalLayout mainArea = new VerticalLayout(title, input, protocolSelector, descriptionAccordion,
                publicAccessBanner, submitButton);
        mainArea.setId(IDs.MAIN_AREA);
        mainArea.addClassNames("main-area", "border", "large-text");
        return mainArea;
    }

    private HorizontalLayout overallArea() {
        Span overallTextStart = new Span("Yalsee already saved ");

        linkCounter = new Span();
        linkCounter.setId(IDs.OVERALL_LINKS_NUMBER);
        Span overallTextEnd = new Span(" links");

        Span overallText = new Span(overallTextStart, linkCounter, overallTextEnd);
        overallText.setId(IDs.OVERALL_LINKS_TEXT);

        HorizontalLayout overallArea = new HorizontalLayout(overallText);
        overallArea.setId(IDs.OVERALL_AREA);
        overallArea.addClassNames("overall-area", "border", "joint-area");
        overallArea.setWidthFull();
        return overallArea;
    }

    private HorizontalLayout resultArea() {
        HorizontalLayout resultArea = new HorizontalLayout();
        resultArea.setId(IDs.RESULT_AREA);

        Span emptySpan = new Span();

        shortLink = new Anchor("", "");
        shortLink.setId(IDs.SHORT_LINK);
        shortLink.addClassName("strong-link");

        com.vaadin.flow.component.icon.Icon copyLinkImage;
        copyLinkImage = new com.vaadin.flow.component.icon.Icon(VaadinIcon.COPY);
        copyLinkImage.setId(IDs.COPY_LINK_BUTTON);
        copyLinkImage.addClickListener(this::copyLinkToClipboard);

        resultArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        resultArea.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        resultArea.add(emptySpan, shortLink, copyLinkImage);
        resultArea.addClassNames("result-area", "border");
        resultArea.setWidthFull();
        return resultArea;
    }

    private Div qrCodeArea() {
        Div qrCodeArea = new Div();
        qrCodeArea.setId(IDs.QR_CODE_AREA);

        qrCode = new Image();
        qrCode.setId(IDs.QR_CODE);
        qrCode.setSrc("");
        qrCode.setAlt("qrCode");

        qrCodeArea.add(qrCode);
        qrCodeArea.addClassNames("qr-area", "border", "joint-area");
        qrCodeArea.setWidthFull();
        return qrCodeArea;
    }

    private HorizontalLayout myLinksNoteArea() {
        HorizontalLayout myLinksNoteArea = new HorizontalLayout();
        myLinksNoteArea.setId(IDs.MY_LINKS_NOTE_AREA);

        Span myLinksNoteText = new Span();

        Span myLinksNoteStart = new Span("FYI: You can find your link and QR Code at ");
        Anchor myLinksNoteLink = new Anchor("/" + Endpoint.UI.MY_LINKS_PAGE, "My Links");
        Span myLinkNoteEnd = new Span(" page");

        myLinksNoteText.setId(IDs.MY_LINKS_NOTE_TEXT);
        myLinksNoteStart.setId(IDs.MY_LINKS_NOTE_START);
        myLinksNoteLink.setId(IDs.MY_LINKS_NOTE_LINK);
        myLinkNoteEnd.setId(IDs.MY_LINKS_NOTE_END);

        myLinksNoteText.add(myLinksNoteStart, myLinksNoteLink, myLinkNoteEnd);
        myLinksNoteArea.add(myLinksNoteText);

        myLinksNoteArea.addClassNames("my-links-note-area", "border", "joint-area");
        myLinksNoteArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        myLinksNoteArea.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        myLinksNoteArea.setWidthFull();

        return myLinksNoteArea;
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
        EventBus.getDefault().register(this);

        //update counter every page load
        updateCounter();
    }

    @Override
    protected void onDetach(final DetachEvent detachEvent) {
        // Cleanup
        super.onDetach(detachEvent);
        ui = null;
        EventBus.getDefault().unregister(this);
    }

    /**
     * Triggers actions when {@link LinkSavedEvent} received.
     *
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkSavedEvent(final LinkSavedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkSavedEvent.class.getSimpleName(), event);
        updateCounter();
    }

    /**
     * Triggers actions when {@link LinkDeletedEvent} received.
     *
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkDeletedEvent(final LinkDeletedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkDeletedEvent.class.getSimpleName(), event);
        updateCounter();
    }

    private void onSaveLink(final ClickEvent<Button> buttonClickEvent) {
        log.trace("{} Submit button clicked. By client? {}", TAG, buttonClickEvent.isFromClient());

        cleanErrors();

        boolean isFormValid = true;
        String longUrl = input.getValue();
        String linkDescription = descriptionInput.getValue();
        log.debug("{} Got long URL: {}", TAG, longUrl);

        if (StringUtils.isBlank(longUrl)) {
            String errorMessage = "Long URL cannot be empty";
            showError(errorMessage);
            isFormValid = false;
        } else {
            try {
                boolean hasProtocol = UrlUtils.hasProtocol(longUrl.trim());
                if (!hasProtocol) {
                    String selectedProtocol = protocolSelector.getValue();
                    if (StringUtils.isNotBlank(selectedProtocol)) {
                        longUrl = selectedProtocol + longUrl.trim();
                    } else {
                        protocolSelector.setVisible(true);
                        protocolSelector.setErrorMessage("Please select protocol");
                        protocolSelector.setInvalid(true);
                        isFormValid = false;
                    }
                }
            } catch (RuntimeException e) {
                log.error("{} URL validation failed", TAG);
                log.debug("", e);
                showError("Got malformed URL or not URL at all");
                isFormValid = false;
            }
        }

        if (isFormValid) {
            cleanForm();
            cleanResults();
            saveLink(longUrl, linkDescription);
        } else {
            log.debug("{} Form is not valid", TAG);
        }
    }

    private void copyLinkToClipboard(
            final ClickEvent<com.vaadin.flow.component.icon.Icon> buttonClickEvent) {
        log.trace("{} Copy link button clicked. From client? {}", TAG, buttonClickEvent.isFromClient());
        ClipboardUtils.copyToClipboardAndNotify(shortLink.getText(),
                "Short link copied", Notification.Position.MIDDLE);
    }

    private void saveLink(final String link, final String linkDescription) {
        String sessionId = YalseeSession.getCurrent().map(YalseeSession::getSessionId).orElse("");
        LinkServiceInput.LinkServiceInputBuilder linkServiceInputBuilder =
                LinkServiceInput.builder(link).sessionID(sessionId);

        if (StringUtils.isNotBlank(linkDescription)) {
            linkServiceInputBuilder.description(linkDescription);
        }

        LinkServiceInput linkServiceInput = linkServiceInputBuilder.build();
        OperationResult saveLinkOperation = linkService.createLink(linkServiceInput);
        if (saveLinkOperation.ok()) {
            onSuccessStoreLink(saveLinkOperation);
        } else {
            onFailStoreLink(saveLinkOperation);
        }
    }

    private void onSuccessStoreLink(final OperationResult successfulResult) {
        cleanErrors();
        cleanForm();

        Link savedLink = successfulResult.getPayload(Link.class);
        log.debug("{} New link successfully saved: {}", TAG, savedLink);
        shortLink.setText(appUtils.getShortUrl() + "/" + savedLink.getIdent());
        shortLink.setHref(appUtils.getShortUrl() + "/" + savedLink.getIdent());
        resultArea.setVisible(true);
        myLinksNoteArea.setVisible(true);
        generateQRCode(savedLink.getIdent());
        scrollToResults();
    }

    private void onFailStoreLink(final OperationResult opResult) {
        log.error("{} Failed to store link. Operation Result: {}", TAG, opResult);
        showError(opResult.getMessage());
    }

    private void updateCounter() {
        if (ui != null) {
            ui.access(() -> linkCounter.setText(Long.toString(overallService.numberOfStoredLinks())));
        }
    }

    private int calculateQRCodeSize() {
        int[] browserWidthInfo = new int[1];
        if (getUI().isPresent()) {
            int[] finalBrowserWidthInfo = browserWidthInfo;
            getUI()
                    .get()
                    .getPage()
                    .retrieveExtendedClientDetails(
                            details -> finalBrowserWidthInfo[0] = details.getScreenWidth());
        } else {
            browserWidthInfo = new int[]{0};
        }
        int browserWidth = browserWidthInfo[0];

        int defaultQRBlockSize = App.QR.DEFAULT_QR_BLOCK_SIZE;
        int defaultQRCodeSize = App.QR.DEFAULT_QR_CODE_SIZE;
        float qrBlockRatio = App.QR.QR_BLOCK_RATIO;

        int size;
        if (browserWidth > defaultQRBlockSize) {
            size = defaultQRCodeSize;
        } else {
            size = Math.round(browserWidth * qrBlockRatio);
        }
        return size;
    }

    private void generateQRCode(final String ident) {
        int size = calculateQRCodeSize();
        OperationResult getQRCodeResult;

        if (size >= App.QR.MINIMAL_SIZE_IN_PIXELS) {
            getQRCodeResult = qrCodeService.getQRCode(ident, size);
        } else {
            getQRCodeResult = qrCodeService.getQRCode(ident);
        }

        if (getQRCodeResult.ok()) {
            onSuccessGenerateQRCode(getQRCodeResult);
        } else {
            onFailGenerateQRCode(getQRCodeResult);
        }
    }

    private void onSuccessGenerateQRCode(final OperationResult goodResult) {
        this.qrCode.setSrc(goodResult.getStringPayload());
        qrCodeArea.setVisible(true);
    }

    private void onFailGenerateQRCode(final OperationResult operationResult) {
        log.error("{} Get QR Code OpResult: {}", TAG, operationResult);
        showError("Internal error. QR generation failed");
        errorUtils.reportToBugsnag(YalseeErrorBuilder
                .withTechMessage(String.format("onFailGenerateQRCode: Operation failed. OpResult: %s", operationResult))
                .withStatus(HttpCode.SERVER_ERROR)
                .build());
        this.qrCode.setSrc("");
        qrCodeArea.setVisible(false);
    }

    private void showError(final String errorMessage) {
        errorNotification = ErrorUtils.getErrorNotification(errorMessage);
        errorNotification.open();
    }

    private void cleanForm() {
        input.setValue("");
        protocolSelector.setVisible(false);
        protocolSelector.setValue("");
        descriptionInput.setValue("");
    }

    private void cleanErrors() {
        if (errorNotification != null && errorNotification.isOpened()) {
            errorNotification.close();
        }
        protocolSelector.setErrorMessage("");
        protocolSelector.setInvalid(false);
    }

    private void cleanResults() {
        shortLink.setHref("");
        shortLink.setText("");
        resultArea.setVisible(false);

        qrCode.setSrc("");
        qrCodeArea.setVisible(false);

        myLinksNoteArea.setVisible(false);
    }

    private void scrollToResults() {
        if (UI.getCurrent().getPage() != null) {
            UI.getCurrent().getPage().executeJs("setTimeout(scrollToResults, 300)");
        }
    }

    private void onInputChanged(final AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        String longLink = event.getValue();
        if (StringUtils.isNotBlank(longLink)) {
            try {
                boolean hasProtocol = UrlUtils.hasProtocol(longLink);
                protocolSelector.setVisible(!hasProtocol);
            } catch (RuntimeException e) {
                log.debug("{} URL validation failed", TAG);
            }
        }
    }

    public static class IDs {
        public static final String VIEW_ID = "homeView";
        public static final String MAIN_AREA = "mainArea";
        public static final String TITLE = "siteTitle";
        public static final String INPUT = "longUrlInput";
        public static final String PROTOCOL_SELECTOR = "protocolSelector";
        public static final String DESCRIPTION_ACCORDION = "descriptionAccordion";
        public static final String DESCRIPTION_INPUT = "descriptionInput";
        public static final String BANNER = "publicAccessBanner";
        public static final String SUBMIT_BUTTON = "submitButton";

        public static final String OVERALL_AREA = "overallArea";
        public static final String OVERALL_LINKS_TEXT = "overallLinksText";
        public static final String OVERALL_LINKS_NUMBER = "overallLinksNum";

        public static final String RESULT_AREA = "resultArea";

        public static final String SHORT_LINK = "shortLink";
        public static final String COPY_LINK_BUTTON = "copyLink";

        public static final String QR_CODE_AREA = "qrCodeArea";
        public static final String QR_CODE = "qrCode";

        public static final String MY_LINKS_NOTE_AREA = "myLinksNoteArea";
        public static final String MY_LINKS_NOTE_TEXT = "myLinksNoteText";
        public static final String MY_LINKS_NOTE_START = "myLinksNoteStart";
        public static final String MY_LINKS_NOTE_LINK = "myLinksNoteLink";
        public static final String MY_LINKS_NOTE_END = "myLinksNoteEnd";
    }
}
