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
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.QRCodeService;
import io.kyberorg.yalsee.services.internal.LinkInfoService;
import io.kyberorg.yalsee.ui.core.EditableLink;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
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

    private final Span sessionBanner = new Span();
    private final Span lonelyBanner = new Span();
    private final Span debugBanner = new Span();
    private final Grid<LinkInfo> grid = new Grid<>(LinkInfo.class);
    private Grid.Column<LinkInfo> linkColumn;
    private Grid.Column<LinkInfo> descriptionColumn;
    private Grid.Column<LinkInfo> qrCodeColumn;
    private Grid.Column<LinkInfo> deleteColumn;
    private Grid.Column<LinkInfo> sessionColumn;

    private Binder<LinkInfo> binder;

    private final LinkInfoService linkInfoService;
    private final QRCodeService qrCodeService;
    private final LinkService linkService;
    private final AppUtils appUtils;

    private UI ui;
    private String sessionId;
    private boolean userModeActivated;

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
        applyLoadState();
    }

    private void init() {
        sessionBanner.setId(IDs.BANNER);
        debugBanner.setId(IDs.DEBUG);
        grid.setId(IDs.GRID);

        sessionId = AppUtils.getSessionId(VaadinSession.getCurrent());
        userModeActivated = false;

        grid.removeAllColumns();

        linkColumn = grid.addColumn(TemplateRenderer.<LinkInfo>of("[[item.shortDomain]]/[[item.ident]]")
                        .withProperty("shortDomain", this::getShortDomain)
                        .withProperty("ident", LinkInfo::getIdent))
                .setHeader("Link");

        descriptionColumn = grid.addColumn(LinkInfo::getDescription).setHeader("Description");
        qrCodeColumn = grid.addComponentColumn(this::qrImage).setHeader("QR Code");
        deleteColumn = grid.addComponentColumn(this::createDeleteButton).setHeader("Actions");
        if (appUtils.isDevelopmentModeActivated()) {
            sessionColumn = grid.addColumn(LinkInfo::getSession).setHeader("Session ID");
        }

        // You can use any renderer for the item details. By default, the
        // details are opened and closed by clicking the rows.
        grid.setItemDetailsRenderer(TemplateRenderer.<LinkInfo>of(
                        "<div class='custom-details' style='border: 1px solid gray; padding: 10px; width: 100%; box-sizing: border-box;'>"
                                + "<div><b><a href=\"[[item.href]]\">[[item.longLink]]</a></b><br>" +
                                "<div>Created: [[item.created]], Updated: [[item.updated]]</div>" +
                                "</div>"
                                + "</div>")
                .withProperty("href", this::getHrefLink)
                .withProperty("longLink", this::getLongLink)
                .withProperty("created", this::getCreatedTime)
                .withProperty("updated", this::getUpdatedTime)
                // This is now how we open the details
                .withEventHandler("handleClick", person -> grid.getDataProvider().refreshItem(person)));

        //binder and editor
        initGridEditor();

        //editor end

        //User-mode activation
        grid.getElement().addEventListener("keydown", event -> {
            userModeActivated = true;
            updateGridEditor();
        }).setFilter("event.key === 'R' && event.shiftKey");

        add(sessionBanner);
        if (appUtils.isDevelopmentModeActivated()) {
            add(debugBanner);
        }
        add(grid);
    }

    private void initGridEditor() {
        binder = new Binder<>(LinkInfo.class);
        grid.getEditor().setBinder(binder);

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

        TextField editDescriptionField = new TextField();
        // Close the editor in case of backward between components
        editDescriptionField.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");

        binder.forField(editDescriptionField).bind("description");
        descriptionColumn.setEditorComponent(editDescriptionField);

        grid.addItemDoubleClickListener(event -> grid.getEditor().editItem(event.getItem()));

        grid.getEditor().addCloseListener(this::updateLinkInfo);
        //Saving by click Enter
        grid.getElement().addEventListener("keydown", event -> {
            grid.getEditor().save();
            grid.getEditor().cancel();
        }).setFilter("event.key === 'Enter'");
    }

    private void updateGridEditor() {
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

    private void applyLoadState() {
        sessionBanner.setText("Those are links stored in current session. " +
                "Soon you will be able to store them permanently, once we introduce users");

        lonelyBanner.setText("It looks lonely here. What about saving something at " + new Anchor("/"));

        if (isGridEmpty()) {
            lonelyBanner.setVisible(false);
        }

        debugBanner.setText("Session ID: " + sessionId);
        debugBanner.setVisible(appUtils.isDevelopmentModeActivated());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
        EventBus.getDefault().register(this);

        updateData();
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
        onLinkEvent(event.getLink());
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
            updateData();
        }
    }

    private void updateData() {
        log.debug("{} updating grid. Current session ID: {}", TAG, sessionId);
        if (ui != null) {
            ui.access(() -> {
                grid.setItems(linkInfoService.getAllRecordWithSession(sessionId));
                lonelyBanner.setVisible(!isGridEmpty());
            });
        }
    }

    private Button createDeleteButton(LinkInfo item) {
        Button deleteButton = new Button("Delete", clickEvent -> onDeleteButtonClick(item));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return deleteButton;
    }

    private void onDeleteButtonClick(LinkInfo item) {
        if (item != null) {
            deleteLinkAndLinkInfo(item);
        } else {
            ErrorUtils.getErrorNotification("Failed to delete: no item selected").open();
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
        updateData();
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

    private Image qrImage(LinkInfo linkInfo) {
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

    private String getShortDomain(LinkInfo linkInfo) {
        return appUtils.getShortDomain();
    }

    private String getHrefLink(LinkInfo linkInfo) {
        return appUtils.getShortUrl() + "/" + linkInfo.getIdent();
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

    private String getTime(Timestamp ts) {
        Date date = new Date(ts.getTime());
        return date.toString();
    }

    private String getSessionIdFromLink(Link linkFromEvent) {
        Optional<LinkInfo> linkInfo = linkInfoService.getLinkInfoByLink(linkFromEvent);
        if (linkInfo.isPresent()) {
            return linkInfo.get().getSession();
        } else {
            return "";
        }
    }

    private boolean isGridEmpty() {
        return grid.getDataProvider().size(new Query<>()) == 0;
    }

    public static class IDs {
        public static final String BANNER = "banner";
        public static final String GRID = "grid";
        public static final String DEBUG = "debugSpan";
    }

}
