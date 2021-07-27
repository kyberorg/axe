package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveEvent;
import com.vaadin.flow.component.html.Div;
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
import io.kyberorg.yalsee.models.LinkInfo;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.QRCodeService;
import io.kyberorg.yalsee.services.internal.LinkInfoService;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.WeakHashMap;

@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.MY_LINKS_PAGE, layout = MainView.class)
@PageTitle("Yalsee: My Links")
public class MyLinksView extends YalseeLayout {
    private final String TAG = "[" + MyLinksView.class.getSimpleName() + "]";

    private final Span sessionBanner = new Span();
    private final Grid<LinkInfo> grid = new Grid<>(LinkInfo.class);
    private Grid.Column<LinkInfo> linkColumn;
    private Grid.Column<LinkInfo> descriptionColumn;
    private Grid.Column<LinkInfo> qrCodeColumn;


    private final LinkInfoService linkInfoService;
    private final QRCodeService qrCodeService;
    private final LinkService linkService;
    private UI ui;

    /**
     * Creates {@link MyLinksView}.
     */
    public MyLinksView(final LinkInfoService linkInfoService, final QRCodeService qrCodeService,
                       final LinkService linkService) {
        this.linkInfoService = linkInfoService;
        this.qrCodeService = qrCodeService;
        this.linkService = linkService;

        setId(MyLinksView.class.getSimpleName());
        init();
        applyLoadState();
    }

    private void init() {
        sessionBanner.setId(IDs.BANNER);
        grid.setId(IDs.GRID);

        grid.removeAllColumns();
        linkColumn = grid.addColumn(LinkInfo::getIdent).setHeader("Link");
        //FIXME implement me: link update
        descriptionColumn = grid.addColumn(LinkInfo::getDescription).setHeader("Description");
        //grid.addEditColumn(LinkInfo::getDescription).text(this::updateLinkInfo)
        //        .setHeader("Description");
       qrCodeColumn = grid.addComponentColumn(this::qrImage).setHeader("QR Code");

        // You can use any renderer for the item details. By default, the
        // details are opened and closed by clicking the rows.
        grid.setItemDetailsRenderer(TemplateRenderer.<LinkInfo>of(
                "<div class='custom-details' style='border: 1px solid gray; padding: 10px; width: 100%; box-sizing: border-box;'>"
                        + "<div><b>[[item.longLink]]</b><br>" +
                        "<div>Created: [[item.created]], Updated: [[item.updated]]</div>" +
                        "</div>"
                        + "</div>")
                .withProperty("longLink", this::getLongLink)
                .withProperty("created", linkInfo -> linkInfo.getCreated().toString())
                .withProperty("updated", linkInfo -> linkInfo.getUpdated().toString())
                // This is now how we open the details
                .withEventHandler("handleClick", person -> grid.getDataProvider().refreshItem(person)));

        //binder and editor
        Binder<LinkInfo> binder = new Binder<>(LinkInfo.class);
        Editor<LinkInfo> editor = grid.getEditor();
        editor.setBuffered(false);
        editor.setBinder(binder);


        Div validationStatus = new Div();
        validationStatus.setId("validation");

        TextField editDescriptionField = new TextField();
        binder.forField(editDescriptionField).bind("description");
        descriptionColumn.setEditorComponent(editDescriptionField);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        Grid.Column<LinkInfo> editorColumn = grid.addComponentColumn(linkInfo -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(linkInfo);
                editDescriptionField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        // Add a keypress listener that listens for an escape key up event.
        // Note! some browsers return key as Escape and some as Esc
        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        editor.addSaveListener(this::onLinkInfoSaved);
        //editor end

        add(sessionBanner, grid);
    }

    private void applyLoadState() {
        sessionBanner.setText("Those are links stored in current session. " +
                "Soon you will be able to store them permanently, once we introduce users");
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
        updateGrid();
    }

    /**
     * Triggers actions when {@link LinkDeletedEvent} received.
     *
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkDeletedEvent(final LinkDeletedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkDeletedEvent.class.getSimpleName(), event);
        updateGrid();
    }

    private void onLinkInfoSaved(EditorSaveEvent<LinkInfo> editorSaveEvent) {
        linkInfoService.update(editorSaveEvent.getItem());
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(linkInfoService.getAllRecordWithSession(AppUtils.getSessionId(VaadinSession.getCurrent())));
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

    private String getLongLink(LinkInfo linkInfo) {
        OperationResult result = linkService.getLinkWithIdent(linkInfo.getIdent());
        if (result.ok()) {
            return result.getStringPayload();
        } else {
            return "";
        }
    }

    public static class IDs {
        public static final String BANNER = "banner";
        public static final String GRID = "grid";
    }

}
