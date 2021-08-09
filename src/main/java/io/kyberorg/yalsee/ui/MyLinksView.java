package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.EditorCloseEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.events.LinkDeletedEvent;
import io.kyberorg.yalsee.events.LinkSavedEvent;
import io.kyberorg.yalsee.events.LinkUpdatedEvent;
import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.models.LinkInfo;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkInfoService;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.QRCodeService;
import io.kyberorg.yalsee.ui.components.EditableLink;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ClipboardUtils;
import io.kyberorg.yalsee.utils.DeviceUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.MY_LINKS_PAGE, layout = MainView.class)
@PageTitle("Yalsee: My Links")
public class MyLinksView extends YalseeLayout {
    private final String TAG = "[" + MyLinksView.class.getSimpleName() + "]";

    private final String USER_MODE_FLAG = "UserMode";

    private final Span sessionBanner = new Span();
    private final Span noRecordsBanner = new Span();
    private final Span noRecordsBannerText = new Span();
    private final Anchor noRecordsBannerLink = new Anchor();

    private final Button endSessionButton = new Button();

    private final Grid<LinkInfo> grid = new Grid<>(LinkInfo.class);
    private Grid.Column<LinkInfo> linkColumn;
    private Grid.Column<LinkInfo> descriptionColumn;
    private Grid.Column<LinkInfo> qrCodeColumn;
    private Grid.Column<LinkInfo> deleteColumn;

    private final LinkInfoService linkInfoService;
    private final QRCodeService qrCodeService;
    private final LinkService linkService;
    private final AppUtils appUtils;

    private final Binder<LinkInfo> binder = new Binder<>(LinkInfo.class);
    private UI ui;
    private String sessionId;
    private DeviceUtils deviceUtils;
    private boolean clientHasSmallScreen;

    /**
     * Creates {@link MyLinksView}.
     */
    public MyLinksView(final LinkInfoService linkInfoService, final QRCodeService qrCodeService,
                       final LinkService linkService, AppUtils appUtils) {
        this.linkInfoService = linkInfoService;
        this.qrCodeService = qrCodeService;
        this.linkService = linkService;
        this.appUtils = appUtils;

        setId(MyLinksView.class.getSimpleName());

        init();
        setIds();
        applyLoadState();
    }

    private void init() {
        sessionId = AppUtils.getSessionId(VaadinSession.getCurrent());
        VaadinSession.getCurrent().setAttribute(USER_MODE_FLAG, Boolean.FALSE);
        deviceUtils = DeviceUtils.createWithUI(UI.getCurrent());
        clientHasSmallScreen = isSmallScreen();

        sessionBanner.setText("Those are links stored in current session. " +
                "Soon you will be able to store them permanently, once we introduce users");

        noRecordsBannerText.setText("It looks lonely here. What about saving something at ");
        noRecordsBannerLink.setHref("/");
        noRecordsBannerLink.setText("MainPage");
        noRecordsBanner.add(noRecordsBannerText, noRecordsBannerLink);

        endSessionButton.setText("End session");
        endSessionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        endSessionButton.addClickListener(this::onCleanButtonClick);
        endSessionButton.getStyle().set("align-self", "flex-end");

        grid.removeAllColumns();

        linkColumn = grid.addComponentColumn(this::link).setHeader("Link");
        descriptionColumn = grid.addColumn(LinkInfo::getDescription).setHeader("Description");
        qrCodeColumn = grid.addComponentColumn(this::qrImage).setHeader("QR Code");
        deleteColumn = grid.addComponentColumn(this::createDeleteButton).setHeader("Actions");

        //Item Details
        grid.setItemDetailsRenderer(TemplateRenderer.<LinkInfo>of(
                        "<div class='" + IDs.ITEM_DETAILS_CLASS
                                + "' style='border: 1px solid gray; padding: 10px; width: 100%; box-sizing: border-box;'>"
                                + "<div><b><a class='"
                                + IDs.ITEM_DETAILS_LINK_CLASS + "' href=\"[[item.href]]\">[[item.longLink]]</a></b><br>" +
                                "<div><span class='"
                                + IDs.ITEM_DETAILS_CREATED_TIME_LABEL_CLASS + "'>Created: </span><span class='"
                                + IDs.ITEM_DETAILS_CREATED_TIME_CLASS + "'>[[item.created]]</span><span class='"
                                + IDs.ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS + "'>, Updated: </span><span class='"
                                + IDs.ITEM_DETAILS_UPDATED_TIME_CLASS + "'>[[item.updated]]</span></div>" +
                                "</div>"
                                + "</div>")
                .withProperty("href", this::getLongLink)
                .withProperty("longLink", this::getLongLink)
                .withProperty("created", this::getCreatedTime)
                .withProperty("updated", this::getUpdatedTime)
                // This is now how we open the details
                .withEventHandler("handleClick", item -> grid.getDataProvider().refreshItem(item)));

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        //User-mode activation
        grid.getElement().addEventListener("keydown", event -> {
                    VaadinSession.getCurrent().setAttribute(USER_MODE_FLAG, Boolean.TRUE);
                    activateLinkEditor();
                })
                .setFilter("event.key === 'R' && event.shiftKey");

        initGridEditor();

        add(sessionBanner, noRecordsBanner, endSessionButton, grid);
    }

    private Span link(final LinkInfo linkInfo) {
        Span link = new Span();
        String shortDomain = appUtils.getShortDomain();
        String ident = linkInfo.getIdent();
        String shortLink = appUtils.getShortUrl() + "/" + ident;

        if (clientHasSmallScreen) {
            link.setText(ident);
        } else {
            link.setText(shortDomain + "/" + ident);
        }

        link.addClickListener(event -> ClipboardUtils.copyLinkToClipboard(shortLink,
                Notification.Position.BOTTOM_CENTER));
        return link;
    }

    private Image qrImage(final LinkInfo linkInfo) {
        Image image = new Image();
        OperationResult qrCodeResult = qrCodeService.getQRCode(linkInfo.getIdent(), App.QR.MINIMAL_SIZE_IN_PIXELS);
        if (qrCodeResult.ok()) {
            image.setSrc(qrCodeResult.getStringPayload());
        }
        image.setAlt("QR Code");
        image.setId(Long.toString(linkInfo.getId()));
        image.addClickListener(this::onQRCodeClicked);
        return image;
    }

    private Button createDeleteButton(final LinkInfo item) {
        Button deleteButton = new Button("Delete", clickEvent -> onDeleteButtonClick(item));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return deleteButton;
    }

    private String getLongLink(LinkInfo linkInfo) {
        OperationResult result = linkService.getLinkWithIdent(linkInfo.getIdent());
        if (result.ok()) {
            return result.getStringPayload();
        } else {
            return "";
        }
    }

    private String getCreatedTime(LinkInfo linkInfo) {
        return getTime(linkInfo.getCreated());
    }

    private String getUpdatedTime(LinkInfo linkInfo) {
        return getTime(linkInfo.getUpdated());
    }

    private void initGridEditor() {
        grid.getEditor().setBinder(binder);

        TextField editDescriptionField = new TextField();
        // Close the editor in case of backward between components
        editDescriptionField.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");

        binder.forField(editDescriptionField).bind("description");
        descriptionColumn.setEditorComponent(editDescriptionField);

        grid.addItemDoubleClickListener(event -> {
            grid.getEditor().editItem(event.getItem());
            editDescriptionField.focus();
        });

        grid.getEditor().addCloseListener(this::updateLinkInfo);
        //Saving by click Enter
        grid.getElement().addEventListener("keydown", event -> {
            grid.getEditor().save();
            grid.getEditor().cancel();
        }).setFilter("event.key === 'Enter'");
    }

    private void activateLinkEditor() {
        boolean userModeActivated = (Boolean) VaadinSession.getCurrent().getAttribute(USER_MODE_FLAG);
        if (userModeActivated) {
            EditableLink editableLink = new EditableLink(appUtils.getShortDomain());
            // Close the editor in case of backward between components
            editableLink.getElement()
                    .addEventListener("keydown",
                            event -> grid.getEditor().cancel())
                    .setFilter("event.key === 'Tab' && event.shiftKey");

            binder.forField(editableLink).bind("ident");

            linkColumn.setEditorComponent(editableLink);
        }
    }

    private void setIds() {
        sessionBanner.setId(IDs.SESSION_BANNER);
        noRecordsBanner.setId(IDs.NO_RECORDS_BANNER);
        noRecordsBannerText.setId(IDs.NO_RECORDS_BANNER_TEXT);
        noRecordsBannerLink.setId(IDs.NO_RECORDS_BANNER_LINK);
        endSessionButton.setId(IDs.END_SESSION_BUTTON);
        grid.setId(IDs.GRID);
        linkColumn.setClassNameGenerator(item -> IDs.LINK_COLUMN_CLASS);
        descriptionColumn.setClassNameGenerator(item -> IDs.DESCRIPTION_COLUMN_CLASS);
        qrCodeColumn.setClassNameGenerator(item -> IDs.QR_CODE_COLUMN_CLASS);
        deleteColumn.setClassNameGenerator(item -> IDs.DELETE_COLUMN_CLASS);
    }

    private void applyLoadState() {
        noRecordsBanner.setVisible(gridIsEmpty());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
        EventBus.getDefault().register(this);

        updateDataAndState();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
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
        onLinkEvent(event.getLink());
    }

    /**
     * Triggers actions when {@link LinkDeletedEvent} received.
     *
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkDeletedEvent(final LinkDeletedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkDeletedEvent.class.getSimpleName(), event);
        //no session control possible since link and its info are gone.
        updateDataAndState();
    }

    /**
     * Triggers actions when {@link LinkUpdatedEvent} received.
     *
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkUpdatedEvent(final LinkUpdatedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkUpdatedEvent.class.getSimpleName(), event);
        onLinkEvent(event.getLink());
    }

    private void onLinkEvent(final Link link) {
        //act only if link deleted was saved within our session
        if (getSessionIdFromLink(link).equals(sessionId)) {
            updateDataAndState();
        }
    }

    private void onDeleteButtonClick(final LinkInfo item) {
        if (item != null) {
            deleteLinkAndLinkInfo(item);
        } else {
            ErrorUtils.getErrorNotification("Failed to delete: no item selected").open();
        }
    }

    private void onCleanButtonClick(ClickEvent<Button> buttonClickEvent) {
        appUtils.endVaadinSession(VaadinSession.getCurrent());
    }

    private void updateDataAndState() {
        log.debug("{} updating grid. Current session ID: {}", TAG, sessionId);
        if (ui != null) {
            ui.access(() -> {
                grid.setItems(linkInfoService.getAllRecordWithSession(sessionId));
                grid.getDataProvider().refreshAll();
                noRecordsBanner.setVisible(gridIsEmpty());
            });
        }
    }

    private void updateLinkInfo(EditorCloseEvent<LinkInfo> event) {
        LinkInfo linkInfo = event.getItem();
        if (linkInfo == null) return;
        Optional<LinkInfo> oldLinkInfo = linkInfoService.getLinkInfoById(linkInfo.getId());
        if (oldLinkInfo.isPresent()) {
            boolean identNotUpdated = linkInfo.getIdent().equals(oldLinkInfo.get().getIdent());
            if (identNotUpdated) {
                linkInfoService.update(linkInfo);
            } else {
                //TODO replace with real-user check once users are there
                boolean userModeActivated = (Boolean) VaadinSession.getCurrent().getAttribute(USER_MODE_FLAG);
                if (userModeActivated) {
                    OperationResult getLinkResult = linkService.getLinkByIdent(oldLinkInfo.get().getIdent());
                    if (getLinkResult.ok()) {
                        Link link = getLinkResult.getPayload(Link.class);
                        link.setIdent(linkInfo.getIdent());
                        OperationResult linkUpdateResult = linkService.updateLink(link);
                        if (linkUpdateResult.ok()) {
                            linkInfoService.update(linkInfo);
                        } else {
                            if (linkUpdateResult.getMessage() == null) {
                                String errorMessage;
                                switch (linkUpdateResult.getResult()) {
                                    case OperationResult.CONFLICT:
                                        errorMessage = "We already have link with given ident. Please try another one";
                                        break;
                                    case OperationResult.SYSTEM_DOWN:
                                        errorMessage = "Failed to update data. System is partly inaccessible. " +
                                                "Try again later";
                                        break;
                                    case OperationResult.GENERAL_FAIL:
                                    default:
                                        errorMessage = "Something unexpected happened at server-side. Try again later.";
                                        break;
                                }
                                ErrorUtils.getErrorNotification(errorMessage).open();
                            } else {
                                ErrorUtils.getErrorNotification(linkUpdateResult.getMessage()).open();
                            }
                        }
                    } else {
                        ErrorUtils.getErrorNotification("Failed to update back-part").open();
                        return;
                    }
                } else {
                    ErrorUtils.getErrorNotification("Back-part updates are allowed only for users. " +
                            "Become user once we introduce them").open();
                }
            }
        } else {
            ErrorUtils.getErrorNotification("Not saved. Internal error: ID mismatch").open();
        }
        updateDataAndState();
    }

    private void deleteLinkAndLinkInfo(LinkInfo linkInfo) {
        if (linkInfo == null) return;
        if (!linkInfo.getSession().equals(this.sessionId)) {
            ErrorUtils.getErrorNotification("Failed to delete link: link from wrong session").open();
            return;
        }

        OperationResult deleteLinkResult = linkService.deleteLinkWithIdent(linkInfo.getIdent());
        //no reason to remove LinkInfo manually as linkService.deleteLinkWithIdent going this.
        //also, no reason to updateData on success as it will be updated by LinkDeletedEvent
        if (deleteLinkResult.ok()) return;
        switch (deleteLinkResult.getResult()) {
            case OperationResult.ELEMENT_NOT_FOUND:
                ErrorUtils.getErrorNotification("Deletion failed: no such link found").open();
                break;
            case OperationResult.SYSTEM_DOWN:
                ErrorUtils.getErrorNotification("System is partly inaccessible. Try again later").open();
                break;
            case OperationResult.GENERAL_FAIL:
            default:
                ErrorUtils.getErrorNotification("Something wrong at server-side. Try again later").open();
                break;
        }
    }

    private void onQRCodeClicked(ClickEvent<Image> imageClickEvent) {
        Optional<Image> bigQRCode = getBigQRCode(imageClickEvent);
        bigQRCode.ifPresentOrElse(qrCode -> {
            Dialog dialog = new Dialog();
            dialog.add(qrCode);
            dialog.open();
        }, this::showNoSuchLinkInfoNotification);
    }

    private void showNoSuchLinkInfoNotification() {
        ErrorUtils.getErrorNotification("Internal Error: No info about stored link found").open();
    }

    private Optional<Image> getBigQRCode(ClickEvent<Image> imageClickEvent) {
        Optional<String> linkInfoId = imageClickEvent.getSource().getId();
        if (linkInfoId.isPresent()) {
            Optional<LinkInfo> linkInfo = linkInfoService.getLinkInfoById(Long.parseLong(linkInfoId.get()));
            if (linkInfo.isPresent()) {
                String ident = linkInfo.get().getIdent();
                OperationResult qrCodeResult = qrCodeService.getQRCode(ident);
                if (qrCodeResult.ok()) {
                    String qrCode = qrCodeResult.getStringPayload();
                    Image image = new Image();
                    image.setSrc(qrCode);
                    image.setAlt("QR Code");
                    return Optional.of(image);
                } else {
                    log.error("{} failed to get QR code {}", TAG, qrCodeResult);
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private String getTime(Timestamp ts) {
        Date date = new Date(ts.getTime());
        return date.toString();
    }

    private String getSessionIdFromLink(Link linkFromEvent) {
        Optional<LinkInfo> linkInfo = linkInfoService.getLinkInfoByLink(linkFromEvent);
        if (linkInfo.isPresent()) {
            return StringUtils.isNotBlank(linkInfo.get().getSession()) ? linkInfo.get().getSession() : "";
        } else {
            return "";
        }
    }

    private boolean gridIsEmpty() {
        return grid.getDataProvider().size(new Query<>()) == 0;
    }

    private boolean isSmallScreen() {
        boolean isSmallScreen = false;
        if (deviceUtils != null && deviceUtils.areDetailsSet()) {
            if (deviceUtils.isExtraSmallDevice()) {
                isSmallScreen = true;
            }
        } else {
            //fallback to user agent detection
            boolean hasBrowserInfo = VaadinSession.getCurrent() != null &&
                    VaadinSession.getCurrent().getBrowser() != null;
            if (hasBrowserInfo && DeviceUtils.isMobileDevice(VaadinSession.getCurrent().getBrowser())) {
                isSmallScreen = true;
            }
        }
        return isSmallScreen;
    }

    public static class IDs {
        public static final String SESSION_BANNER = "sessionBanner";

        public static final String NO_RECORDS_BANNER = "noRecordsBanner";
        public static final String NO_RECORDS_BANNER_TEXT = "noRecordsBannerText";
        public static final String NO_RECORDS_BANNER_LINK = "noRecordsBannerLink";

        public static final String END_SESSION_BUTTON = "endSessionButton";

        public static final String GRID = "grid";
        public static final String LINK_COLUMN_CLASS = "linkCol";
        public static final String DESCRIPTION_COLUMN_CLASS = "descriptionCol";
        public static final String QR_CODE_COLUMN_CLASS = "qrCodeCol";
        public static final String DELETE_COLUMN_CLASS = "deleteCol";
        public static final String ITEM_DETAILS_CLASS = "item-details";
        public static final String ITEM_DETAILS_LINK_CLASS = "long-link";
        public static final String ITEM_DETAILS_CREATED_TIME_LABEL_CLASS = "created-time-label";
        public static final String ITEM_DETAILS_CREATED_TIME_CLASS = "created-time";
        public static final String ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS = "updated-time-label";
        public static final String ITEM_DETAILS_UPDATED_TIME_CLASS = "updated-time";
    }
}
