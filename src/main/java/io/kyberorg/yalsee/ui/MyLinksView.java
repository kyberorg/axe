package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
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
    private final Span debugBanner = new Span();
    private final Grid<LinkInfo> grid = new Grid<>(LinkInfo.class);
    private Grid.Column<LinkInfo> linkColumn;
    private Grid.Column<LinkInfo> descriptionColumn;
    private Grid.Column<LinkInfo> qrCodeColumn;
    private Grid.Column<LinkInfo> sessionColumn;


    private final LinkInfoService linkInfoService;
    private final QRCodeService qrCodeService;
    private final LinkService linkService;
    private final AppUtils appUtils;
    private UI ui;
    private String sessionId;

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

        grid.removeAllColumns();

        linkColumn = grid.addColumn(TemplateRenderer.<LinkInfo>of("[[item.shortDomain]]/[[item.ident]]")
                .withProperty("shortDomain", this::getShortDomain)
                .withProperty("ident", LinkInfo::getIdent))
                .setHeader("Link");

        descriptionColumn = grid.addColumn(LinkInfo::getDescription).setHeader("Description");
        qrCodeColumn = grid.addComponentColumn(this::qrImage).setHeader("QR Code");
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
        Binder<LinkInfo> binder = new Binder<>(LinkInfo.class);
        grid.getEditor().setBinder(binder);

        EditableLink editableLink = new EditableLink(appUtils.getShortDomain());
        // Close the editor in case of backward between components
        editableLink.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");

        binder.forField(editableLink).bind("ident");


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

        grid.getEditor().addCloseListener(event -> {
            if (binder.getBean() != null) {
                linkInfoService.update(binder.getBean());
                updateGrid();
            }
        });
        //Saving by click Enter
        grid.getElement().addEventListener("keydown", event -> {
            grid.getEditor().save();
            grid.getEditor().cancel();
        }).setFilter("event.key === 'Enter'");

        //Saving by clicking mouse
        grid.getElement().addEventListener("click", event -> {
            grid.getEditor().save();
            grid.getEditor().cancel();
        });
        //editor end

        add(sessionBanner);
        if (appUtils.isDevelopmentModeActivated()) {
            add(debugBanner);
        }
        add(grid);
    }

    private void applyLoadState() {
        sessionBanner.setText("Those are links stored in current session. " +
                "Soon you will be able to store them permanently, once we introduce users");

        if (appUtils.isDevelopmentModeActivated()) {
            debugBanner.setText("Session ID: " + sessionId);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
        EventBus.getDefault().register(this);

        updateGrid();
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
        //act only if link saved within our session
        if (getSessionIdFromLink(event.getLink()).equals(sessionId)) {
            updateGrid();
        }
    }

    /**
     * Triggers actions when {@link LinkDeletedEvent} received.
     *
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkDeletedEvent(final LinkDeletedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkDeletedEvent.class.getSimpleName(), event);
        //act only if link deleted was saved within our session
        if (getSessionIdFromLink(event.getLink()).equals(sessionId)) {
            updateGrid();
        }
    }

    private void updateGrid() {
        log.debug("{} updating grid. Current session ID: {}", TAG, sessionId);
        grid.setItems(linkInfoService.getAllRecordWithSession(sessionId));
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

    public static class IDs {
        public static final String BANNER = "banner";
        public static final String GRID = "grid";
        public static final String DEBUG = "debugSpan";
    }

}
