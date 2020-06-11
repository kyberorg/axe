package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.exception.error.YalsErrorBuilder;
import eu.yals.json.StoreRequestJson;
import eu.yals.services.overall.OverallService;
import eu.yals.utils.AppUtils;
import eu.yals.utils.ErrorUtils;
import eu.yals.utils.push.Broadcaster;
import eu.yals.utils.push.Push;
import eu.yals.utils.push.PushCommand;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.olli.ClipboardHelper;

import static eu.yals.constants.HttpCode.STATUS_200;
import static eu.yals.constants.HttpCode.STATUS_201;
import static eu.yals.utils.push.PushCommand.UPDATE_COUNTER;

@Slf4j
@SpringComponent
@UIScope
@CssImport("./css/home_view.css")
@Route(value = Endpoint.UI.HOME_PAGE, layout = AppView.class)
@Caption("Home")
@Icon(VaadinIcon.HOME)
@PageTitle("Link shortener for friends")
public class HomeView extends VerticalLayout {
    private static final String TAG = "[" + HomeView.class.getSimpleName() + "]";

    private final Board board = new Board();
    private final Row firstRow = new Row();
    private final Row mainRow = new Row();
    private final Row overallRow = new Row();
    private final Row resultRow = new Row();
    private final Row qrCodeRow = new Row();

    private final OverallService overallService;
    private final AppUtils appUtils;
    private final ErrorUtils errorUtils;
    private Registration broadcasterRegistration;

    private TextField input;
    private Button submitButton;
    private Anchor shortLink;
    private ClipboardHelper clipboardHelper;
    private Image qrCode;

    private Span linkCounter;

    private Notification errorNotification;

    /**
     * Create {@link HomeView}.
     *
     * @param overallService overall service for getting number of links
     * @param appUtils       application utils for getting server location and API location
     * @param errorUtils     error utils to report to bugsnag
     */
    public HomeView(
            final OverallService overallService, final AppUtils appUtils, final ErrorUtils errorUtils) {
        this.overallService = overallService;
        this.appUtils = appUtils;
        this.errorUtils = errorUtils;

        init();
        applyStyle();
        applyLoadState();
    }

    private void init() {
        this.setId(IDs.VIEW_ID);

        mainRow.add(emptyDiv(), mainArea(), emptyDiv());
        overallRow.add(emptyDiv(), overallArea(), emptyDiv());
        resultRow.add(emptyDiv(), resultArea(), emptyDiv());
        qrCodeRow.add(emptyDiv(), qrCodeArea(), emptyDiv());

        board.addRow(firstRow);
        board.addRow(mainRow);
        board.addRow(overallRow);
        board.addRow(resultRow);
        board.addRow(qrCodeRow);

        add(board);
    }

    private void applyStyle() {
        mainRow.setComponentSpan(mainRow.getComponentAt(1), 2);
        mainRow.addClassName("row");

        overallRow.setComponentSpan(overallRow.getComponentAt(1), 2);
        overallRow.addClassName("row");

        resultRow.setComponentSpan(resultRow.getComponentAt(1), 2);
        resultRow.addClassName("row");

        qrCodeRow.setComponentSpan(qrCodeRow.getComponentAt(1), 2);
        qrCodeRow.addClassName("row");

        board.setSizeFull();
    }

    private void applyLoadState() {
        long linksStored = overallService.numberOfStoredLinks();
        linkCounter.setText(Long.toString(linksStored));

        input.setAutofocus(true);
        input.setValueChangeMode(ValueChangeMode.TIMEOUT);
        input.addValueChangeListener(event -> updateButtonState());

        mainRow.setVisible(true);
        overallRow.setVisible(true);
        resultRow.setVisible(false);
        qrCodeRow.setVisible(false);
    }

    private Div emptyDiv() {
        Div div = new Div();
        div.setText("");
        return div;
    }

    private VerticalLayout mainArea() {
        H2 title = new H2("Yet another link shortener");
        title.setId(IDs.TITLE);

        Span subtitle = new Span("... for friends");
        subtitle.setId(IDs.SUBTITLE);
        subtitle.addClassName("italic");

        input = new TextField("Your very long URL here:");
        input.setId(IDs.INPUT);
        input.setPlaceholder("http://mysuperlongurlhere.tld");
        input.setWidthFull();

        Span publicAccessBanner =
                new Span("Note: all links considered as public and can be used by anyone");
        publicAccessBanner.setId(IDs.BANNER);

        submitButton = new Button("Shorten it!");
        submitButton.setId(IDs.SUBMIT_BUTTON);
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(this::onSaveLink);

        VerticalLayout mainArea =
                new VerticalLayout(title, subtitle, input, publicAccessBanner, submitButton);
        mainArea.setId(IDs.MAIN_AREA);
        mainArea.addClassNames("main-area", "border");
        return mainArea;
    }

    private HorizontalLayout overallArea() {
        Span overallTextStart = new Span("Yals already saved ");

        linkCounter = new Span();
        linkCounter.setId(IDs.OVERALL_LINKS_NUMBER);
        Span overallTextEnd = new Span(" links");

        Span overallText = new Span(overallTextStart, linkCounter, overallTextEnd);
        overallText.setId(IDs.OVERALL_LINKS_TEXT);

        HorizontalLayout overallArea = new HorizontalLayout(overallText);
        overallArea.setId(IDs.OVERALL_AREA);
        overallArea.addClassNames("overall-area", "border");
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
        copyLinkImage.addClickListener(this::copyLinkToClipboard);

        clipboardHelper = new ClipboardHelper();
        clipboardHelper.wrap(copyLinkImage);
        clipboardHelper.setId(IDs.COPY_LINK_BUTTON);

        resultArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        resultArea.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        resultArea.add(emptySpan, shortLink, clipboardHelper);
        resultArea.addClassNames("result-area", "border");
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
        qrCodeArea.addClassNames("qr-area", "border");
        return qrCodeArea;
    }

    private Notification getErrorNotification(final String text) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);

        HorizontalLayout layout = new HorizontalLayout();
        Label label = new Label(text);
        Button closeButton = new Button("OK", event -> notification.close());

        label.getStyle().set("margin-right", "0.5rem");
        closeButton.getStyle().set("margin-right", "0.5rem");

        layout.add(label, closeButton);
        notification.add(layout);
        return notification;
    }

    private Notification getLinkCopiedNotification() {
        final int notificationDuration = 3000;
        Notification notification =
                new Notification("Short link copied", notificationDuration, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        return notification;
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(message -> ui.access(() -> {
            log.debug("{} Push received. {} ID: {}, Message: {}",
                    TAG, HomeView.class.getSimpleName(), ui.getUIId(), message);
            Push push = Push.fromMessage(message);
            if (push.valid()) {
                PushCommand command = push.getPushCommand();
                if (command == UPDATE_COUNTER) {
                    updateCounter();
                } else {
                    log.warn("{} got unknown push command: '{}'", TAG, push.getPushCommand());
                }
            } else {
                log.debug("{} not valid push command: '{}'", TAG, message);
            }
        }));
    }

    @Override
    protected void onDetach(final DetachEvent detachEvent) {
        // Cleanup
        log.debug("{} {} {} detached", TAG, HomeView.class.getSimpleName(), detachEvent.getUI().getUIId());
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private void onSaveLink(final ClickEvent<Button> buttonClickEvent) {
        log.trace("{} Submit button clicked. By client? {}", TAG, buttonClickEvent.isFromClient());

        cleanErrors();
        cleanResults();

        boolean isFormValid = true;
        String longUrl = input.getValue();
        log.debug("{} Got long URL: {}", TAG, longUrl);
        cleanForm();

        if (StringUtils.isBlank(longUrl)) {
            String errorMessage = "Long URL cannot be empty";
            showError(errorMessage);
            isFormValid = false;
        } else {
            try {
                longUrl = AppUtils.makeFullUri(longUrl).toString();
            } catch (RuntimeException e) {
                log.error("{} URL validation failed", TAG);
                log.debug("", e);
                showError("Got malformed URL or not URL at all");
                isFormValid = false;
            }
        }

        if (isFormValid) {
            cleanResults();
            sendLink(longUrl);
        } else {
            log.debug("{} Form is not valid", TAG);
        }
    }

    private void copyLinkToClipboard(
            final ClickEvent<com.vaadin.flow.component.icon.Icon> buttonClickEvent) {
        log.trace("{} Copy link button clicked. From client? {}", TAG, buttonClickEvent.isFromClient());
        getLinkCopiedNotification().open();
        //All other actions are performed by component wrapper
    }

    private void sendLink(final String link) {
        final String apiRoute = Endpoint.Api.STORE_API;
        StoreRequestJson json = StoreRequestJson.create().withLink(link);
        HttpResponse<JsonNode> response =
                Unirest.post(appUtils.getAPIHostPort() + apiRoute).body(json).asJson();
        log.debug("{} Got reply from Store API. Status: {}, Body: {}",
                TAG, response.getStatus(), response.getBody().toPrettyString());
        if (response.isSuccess()) {
            onSuccessStoreLink(response);
        } else {
            onFailStoreLink(response);
        }
    }

    private void onSuccessStoreLink(final HttpResponse<JsonNode> response) {
        cleanErrors();
        cleanForm();
        if (response.getStatus() == STATUS_201) {
            JsonNode json = response.getBody();
            String ident = json.getObject().getString("ident");
            log.debug("{} Got reply with ident: {}", TAG, ident);
            if (StringUtils.isNotBlank(ident)) {
                shortLink.setText(appUtils.getServerUrl() + "/" + ident);
                shortLink.setHref(ident);
                resultRow.setVisible(true);
                clipboardHelper.setContent(shortLink.getText());
                Broadcaster.broadcast(Push.command(UPDATE_COUNTER).toString());
                generateQRCode(ident);
            } else {
                showError("Internal error. Got malformed reply from server");
                errorUtils.reportToBugsnag(YalsErrorBuilder
                        .withTechMessage(String.format("onSuccessStoreLink: Malformed JSON: %s",
                                response.getBody().toPrettyString()
                        ))
                        .withStatus(response.getStatus())
                        .build());
            }
        } else {
            log.error("{} Got false positive. Status: {}, Body: {}",
                    TAG, response.getStatus(), response.getBody().toPrettyString());

            showError("Something wrong was happened at server-side. Issue already reported");
            errorUtils.reportToBugsnag(YalsErrorBuilder
                    .withTechMessage(String.format("onSuccessStoreLink: Got false positive. Body: %s",
                            response.getBody().toPrettyString()
                    ))
                    .withStatus(response.getStatus()).build());
        }
    }

    private void onFailStoreLink(final HttpResponse<JsonNode> response) {
        JsonNode json = response.getBody();
        log.error("{} Failed to store link. Reply: {}", TAG, json);
        String message;
        try {
            message = json.getObject().getString("message");
        } catch (JSONException e) {
            log.error("{} Malformed Error Json", TAG);
            log.debug("", e);
            message = "Hups. Something went wrong at server-side";
            errorUtils.reportToBugsnag(YalsErrorBuilder
                    .withTechMessage(String.format("onFailStoreLink: Malformed JSON. Body: %s",
                            response.getBody().toPrettyString()
                    ))
                    .withStatus(response.getStatus()).addRawException(e)
                    .build());
        }

        showError(message);
    }

    private void updateCounter() {
        linkCounter.setText(Long.toString(overallService.numberOfStoredLinks()));
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
        String qrCodeGeneratorRoute;
        if (size == 0) {
            qrCodeGeneratorRoute =
                    String.format("%s/%s/%s", appUtils.getAPIHostPort(), Endpoint.Api.QR_CODE_API, ident);
        } else {
            qrCodeGeneratorRoute =
                    String.format(
                            "%s/%s/%s/%d", appUtils.getAPIHostPort(), Endpoint.Api.QR_CODE_API, ident, size);
        }

        HttpResponse<JsonNode> response = Unirest.get(qrCodeGeneratorRoute).asJson();
        log.debug("{} Got reply from QR Code API. Status: {}, Body: {}",
                TAG, response.getStatus(), response.getBody().toPrettyString());
        if (response.isSuccess()) {
            onSuccessGenerateQRCode(response);
        } else {
            onFailGenerateQRCode(response);
        }
    }

    private void onSuccessGenerateQRCode(final HttpResponse<JsonNode> response) {
        if (response.getStatus() == STATUS_200) {
            String qrCode = response.getBody().getObject().getString("qr_code");
            if (StringUtils.isNotBlank(qrCode)) {
                this.qrCode.setSrc(qrCode);
                qrCodeRow.setVisible(true);
            } else {
                showError("Internal error. Got malformed reply from QR generator");
                errorUtils.reportToBugsnag(YalsErrorBuilder
                        .withTechMessage(String.format("onSuccessGenerateQRCode: Malformed JSON. Body: %s",
                                response.getBody().toPrettyString()
                        ))
                        .withStatus(response.getStatus())
                        .build());
            }
        } else {
            showError("Internal error. Something is wrong at server-side");
            errorUtils.reportToBugsnag(YalsErrorBuilder
                    .withTechMessage(String.format("onSuccessGenerateQRCode: False positive. Body: %s",
                            response.getBody().toPrettyString()
                    ))
                    .withStatus(response.getStatus())
                    .build());
        }
    }

    private void onFailGenerateQRCode(final HttpResponse<JsonNode> response) {
        showError("Internal error. Got malformed reply from QR generator");
        errorUtils.reportToBugsnag(YalsErrorBuilder
                .withTechMessage(String.format("onFailGenerateQRCode: Malformed JSON. Body: %s",
                        response.getBody().toPrettyString()
                ))
                .withStatus(response.getStatus())
                .build());
        this.qrCode.setSrc("");
        qrCodeRow.setVisible(false);

        if (response.getBody() != null) {
            log.error("{} QR Code Reply JSON: {}", TAG, response.getBody());
        }
    }

    private void showError(final String errorMessage) {
        errorNotification = getErrorNotification(errorMessage);
        errorNotification.open();
    }

    private void cleanForm() {
        input.setValue("");
    }

    private void cleanErrors() {
        if (errorNotification != null && errorNotification.isOpened()) {
            errorNotification.close();
        }
    }

    private void cleanResults() {
        shortLink.setHref("");
        shortLink.setText("");
        resultRow.setVisible(false);

        qrCode.setSrc("");
        qrCodeRow.setVisible(false);
    }

    private void updateButtonState() {
        boolean inputHasValue = StringUtils.isNotBlank(input.getValue());
        submitButton.setEnabled(inputHasValue);
    }

    public static class IDs {
        public static final String VIEW_ID = "homeView";
        public static final String MAIN_AREA = "mainArea";
        public static final String TITLE = "siteTitle";
        public static final String SUBTITLE = "subTitle";
        public static final String INPUT = "longUrlInput";
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
    }

}
