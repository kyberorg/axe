package pm.axe.ui.pages.mylinks;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.EditorSaveEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import pm.axe.Axe;
import pm.axe.Axe.Session;
import pm.axe.Endpoint;
import pm.axe.core.IdentValidator;
import pm.axe.db.models.Link;
import pm.axe.db.models.LinkInfo;
import pm.axe.db.models.User;
import pm.axe.events.link.LinkDeletedEvent;
import pm.axe.events.link.LinkSavedEvent;
import pm.axe.events.link.LinkUpdatedEvent;
import pm.axe.events.linkinfo.LinkInfoUpdatedEvent;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkInfoService;
import pm.axe.services.LinkService;
import pm.axe.services.QRCodeService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.DeleteConfirmationDialog;
import pm.axe.ui.elements.MobileShareMenu;
import pm.axe.ui.elements.ShareMenu;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ClipboardUtils;
import pm.axe.utils.DeviceUtils;
import pm.axe.utils.ErrorUtils;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@SpringComponent
@UIScope
@CssImport("./css/my_links_page.css")
@Route(value = Endpoint.UI.MY_LINKS_PAGE, layout = MainView.class)
@PageTitle("My Links - Axe.pm")
public class MyLinksPage extends AxeBaseLayout implements BeforeEnterObserver {
    private static final String TAG = "[" + MyLinksPage.class.getSimpleName() + "]";

    private final Span sessionBanner = new Span();
    private final Span noRecordsBanner = new Span();
    private final Span noRecordsBannerText = new Span();
    private final Anchor noRecordsBannerLink = new Anchor();

    private final Div filterAndToggleLayout = new Div();
    private final TextField gridFilterField = new TextField();
    private final Button toggleColumnsButton = new Button();
    private final ColumnToggleContextMenu columnToggleMenu = new ColumnToggleContextMenu(toggleColumnsButton);
    private final ShareMenu shareMenu = ShareMenu.create();

    private final Grid<LinkInfo> grid = new Grid<>(LinkInfo.class);
    private Grid.Column<LinkInfo> linkColumn;
    private Grid.Column<LinkInfo> descriptionColumn;
    private Grid.Column<LinkInfo> qrCodeColumn;
    private Grid.Column<LinkInfo> actionsColumn;
    private EditableLink editableLink;

    private final Notification savedNotification = createSavedNotification();

    private final LinkInfoService linkInfoService;
    private final QRCodeService qrCodeService;
    private final LinkService linkService;
    private final AppUtils appUtils;
    private final IdentValidator identValidator;

    private final Binder<LinkInfo> binder = new Binder<>(LinkInfo.class);
    private UI ui;
    private String sessionId;
    private DeviceUtils deviceUtils;
    private boolean clientHasSmallScreen;

    private boolean userLoggedIn;
    private User user;

    private boolean editorAlreadyActivated = false;

    @PostConstruct
    private void pageInit() {
        initElements();
        setPageStructure();
        setIds();
    }

    private void initElements() {
        sessionBanner.setText("Those are links stored in current session (30 minutes). "
                + "Soon you will be able to store them permanently, once we introduce users.");

        noRecordsBannerText.setText("It looks lonely here. What about saving something at ");
        noRecordsBannerLink.setHref("/");
        noRecordsBannerLink.setText("MainPage");

        filterAndToggleLayout.setWidthFull();

        final Icon searchIcon = VaadinIcon.SEARCH.create();
        searchIcon.setId("searchIcon");

        gridFilterField.setMaxWidth("50%");
        gridFilterField.setPlaceholder("Search");
        gridFilterField.setPrefixComponent(searchIcon);
        gridFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        gridFilterField.setClearButtonVisible(true);
        gridFilterField.getStyle().set("align-self", "flex-start");

        toggleColumnsButton.setText("Show/Hide Columns");
        toggleColumnsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        toggleColumnsButton.getStyle().set("align-self", "flex-end");

        initGrid();
        initGridEditor();

        //those actions should be performed after initGrid()
        gridFilterField.addValueChangeListener(e -> grid.getDataProvider().refreshAll());
        columnToggleMenu.addColumnsFromGrid(grid);
    }

    private void setPageStructure() {
        filterAndToggleLayout.add(gridFilterField, toggleColumnsButton);
        noRecordsBanner.add(noRecordsBannerText, noRecordsBannerLink);
        add(sessionBanner, noRecordsBanner, filterAndToggleLayout, grid);
    }

    private void setIds() {
        setId(MyLinksPage.class.getSimpleName());
        sessionBanner.setId(IDs.SESSION_BANNER);
        noRecordsBanner.setId(IDs.NO_RECORDS_BANNER);
        noRecordsBannerText.setId(IDs.NO_RECORDS_BANNER_TEXT);
        noRecordsBannerLink.setId(IDs.NO_RECORDS_BANNER_LINK);
        filterAndToggleLayout.setId("filterAndToggleLayout");
        gridFilterField.setId("gridFilterField");
        toggleColumnsButton.setId("toggleColumnsButton");
        grid.setId(IDs.GRID);
        linkColumn.setClassNameGenerator(item -> IDs.LINK_COLUMN_CLASS);
        descriptionColumn.setClassNameGenerator(item -> IDs.DESCRIPTION_COLUMN_CLASS);
        qrCodeColumn.setClassNameGenerator(item -> IDs.QR_CODE_COLUMN_CLASS);
        actionsColumn.setClassNameGenerator(item -> "actionsCol");
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        initUserState();
        applyLoadState();
        activateLinkEditor();

        sessionId = AxeSession.getCurrent().map(AxeSession::getSessionId).orElse(Session.EMPTY_ID);
        deviceUtils = DeviceUtils.createWithUI(UI.getCurrent());
        clientHasSmallScreen = isSmallScreen();
    }

    private void initUserState() {
        this.userLoggedIn = AxeSession.getCurrent().map(AxeSession::hasUser).orElse(false);
        if (this.userLoggedIn) {
            this.user = AxeSession.getCurrent().get().getUser();
        }
    }

    private void applyLoadState() {
        noRecordsBanner.setVisible(gridIsEmpty());
        if (userLoggedIn) {
            sessionBanner.setText("My Links");
            sessionBanner.setClassName("grid-title");
        }
    }

    private void initGrid() {
        //remove default columns - first
        grid.removeAllColumns();

        linkColumn = grid.addComponentColumn(this::link).setHeader("Link").setKey("Link");
        descriptionColumn = grid.addColumn(LinkInfo::getDescription).setHeader("Description").setKey("Description");
        qrCodeColumn = grid.addComponentColumn(this::qrImage).setHeader("QR Code").setKey("QR Code");
        if (isSmallScreen()) {
            actionsColumn = grid.addComponentColumn(this::createActionsForSmallScreens);
        } else {
            actionsColumn = grid.addComponentColumn(this::createActions);
        }
        actionsColumn.setHeader("Actions").setKey("Actions");

        grid.setItemDetailsRenderer(LitRenderer.<LinkInfo>of(
                        "<div class='" + IDs.ITEM_DETAILS_CLASS
                                + "' style='border: 1px solid gray; padding: 10px; width: 100%; "
                                + "box-sizing: border-box;'>"
                                + "<div><b><a class='"
                                + IDs.ITEM_DETAILS_LINK_CLASS + "' href=\"${item.href}\">${item.longLink}</a></b>"
                                + "<br>"
                                + "<div><span class='"
                                + IDs.ITEM_DETAILS_CREATED_TIME_LABEL_CLASS + "'>Created: </span><span class='"
                                + IDs.ITEM_DETAILS_CREATED_TIME_CLASS + "'>${item.created}</span><span class='"
                                + IDs.ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS + "'>, Updated: </span><span class='"
                                + IDs.ITEM_DETAILS_UPDATED_TIME_CLASS + "'>${item.updated}</span></div>"
                                + "</div>"
                                + "</div>")
                .withProperty("href", this::getLongLink)
                .withProperty("longLink", this::getLongLink)
                .withProperty("created", this::getCreatedTime)
                .withProperty("updated", this::getUpdatedTime)
                // This is now how we open the details
                .withFunction("handleClick", item -> grid.getDataProvider().refreshItem(item)));

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
    }


    private Span link(final LinkInfo linkInfo) {
        Span link = new Span();
        String shortDomain = appUtils.getShortDomain();
        String ident = linkInfo.getIdent();
        String shortLink = appUtils.getShortUrl() + "/" + ident;

        //adding copy to clipboard stuff
        ClipboardUtils.setCopyToClipboardFunctionFor(link);
        ClipboardUtils.setTextToCopy(shortLink).forComponent(link);

        if (clientHasSmallScreen) {
            link.setText(ident);
        } else {
            link.setText(shortDomain + "/" + ident);
        }

        link.addClickListener(event -> ClipboardUtils.showLinkCopiedNotification("Short link copied",
                Notification.Position.BOTTOM_CENTER));
        return link;
    }

    private Image qrImage(final LinkInfo linkInfo) {
        Image image = new Image();
        OperationResult qrCodeResult = qrCodeService.getQRCode(linkInfo.getIdent(), Axe.QR.MINIMAL_SIZE_IN_PIXELS);
        if (qrCodeResult.ok()) {
            image.setSrc(qrCodeResult.getStringPayload());
        }
        image.setAlt("QR Code");
        image.setId(Long.toString(linkInfo.getId()));
        image.addClickListener(this::onQRCodeClicked);
        return image;
    }

    private HorizontalLayout createActions(final LinkInfo item) {
        Button shareButton = createShareButton(item);
        Button editButton = createEditButton(item);
        Button deleteButton = createDeleteButton(item);
        Button saveButton = createSaveButton(item);
        Button cancelButton = createCancelButton(item);

        HorizontalLayout actionsLayout = new HorizontalLayout();
        if (grid.getEditor().isOpen()) {
            actionsLayout.add(saveButton, cancelButton);
            shareButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        } else {
            actionsLayout.add(shareButton, editButton, deleteButton);
            shareButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        return actionsLayout;
    }

    private Button createShareButton(final LinkInfo item) {
        Button shareButton = new Button("Share", clickEvent -> onShareButtonClick(item));
        shareButton.setClassName("share-btn");
        shareButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        return shareButton;
    }

    private Button createEditButton(final LinkInfo item) {
        Button editButton = new Button("Edit", clickEvent -> onEditButtonClick(item));
        editButton.setClassName("edit-btn");
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return editButton;
    }

    private Button createDeleteButton(final LinkInfo item) {
        Button deleteButton = new Button("Delete", clickEvent -> onDeleteButtonClick(item));
        deleteButton.setClassName("delete-btn");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return deleteButton;
    }

    private Button createSaveButton(final LinkInfo item) {
        Button saveButton = new Button("Save", clickEvent -> onSaveButtonClick(item));
        saveButton.setClassName("save-btn");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        return saveButton;
    }

    private Button createCancelButton(final LinkInfo item) {
        Button cancelButton = new Button("Cancel", clickEvent -> onCancelButtonClick(item));
        cancelButton.setClassName("cancel-btn");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return cancelButton;
    }

    private HorizontalLayout createActionsForSmallScreens(final LinkInfo item) {
        Icon shareIcon = createShareIcon(item);
        Icon editIcon = createEditIcon(item);
        Icon deleteIcon = createDeleteIcon(item);
        Icon saveIcon = createSaveIcon(item);
        Icon cancelIcon = createCancelIcon(item);

        HorizontalLayout layout = new HorizontalLayout();
        if (grid.getEditor().isOpen()) {
            layout.add(saveIcon, cancelIcon);
            shareIcon.setClassName("secondary");
            saveIcon.setClassName("success");
            cancelIcon.setClassName("tertiary");
        } else {
            layout.add(shareIcon, editIcon, deleteIcon);
            shareIcon.setClassName("success");
            editIcon.setClassName("primary");
            deleteIcon.setClassName("danger");
        }
        return layout;
    }

    private Icon createShareIcon(final LinkInfo item) {
        Icon shareIcon = new Icon(VaadinIcon.SHARE);
        shareIcon.addClickListener(clickEvent -> onShareButtonClick(item));
        shareIcon.setClassName("share-icon");
        return shareIcon;
    }

    private Icon createEditIcon(final LinkInfo item) {
        Icon editIcon = new Icon(VaadinIcon.EDIT);
        editIcon.addClickListener(clickEvent -> onEditButtonClick(item));
        editIcon.setClassName("edit-icon");
        return editIcon;
    }

    private Icon createDeleteIcon(final LinkInfo item) {
        Icon deleteIcon = new Icon(VaadinIcon.TRASH);
        deleteIcon.addClickListener(clickEvent -> onDeleteButtonClick(item));
        deleteIcon.setClassName("delete-icon");
        return deleteIcon;
    }

    private Icon createSaveIcon(final LinkInfo item) {
        Icon saveIcon = new Icon(VaadinIcon.ADD_DOCK);
        saveIcon.addClickListener(clickEvent -> onSaveButtonClick(item));
        saveIcon.setClassName("save-icon");
        return saveIcon;
    }

    private Icon createCancelIcon(final LinkInfo item) {
        Icon cancelIcon = new Icon(VaadinIcon.CLOSE_CIRCLE);
        cancelIcon.addClickListener(clickEvent -> onCancelButtonClick(item));
        cancelIcon.setClassName("cancel-icon");
        return cancelIcon;
    }


    private String getLongLink(final LinkInfo linkInfo) {
        OperationResult result = linkService.getLinkWithIdent(linkInfo.getIdent());
        if (result.ok()) {
            return result.getStringPayload();
        } else {
            return "";
        }
    }

    private String getCreatedTime(final LinkInfo linkInfo) {
        return getTime(linkInfo.getCreated());
    }

    private String getUpdatedTime(final LinkInfo linkInfo) {
        return getTime(linkInfo.getUpdated());
    }

    private void initGridEditor() {
        grid.getEditor().setBinder(binder);
        grid.getEditor().setBuffered(true);

        TextField editDescriptionField = new TextField();
        // Close the editor in case of backward between components
        editDescriptionField.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");

        binder.forField(editDescriptionField).bind("description");
        descriptionColumn.setEditorComponent(editDescriptionField);

        grid.addItemDoubleClickListener(event -> {
            if (grid.getEditor().isOpen()) grid.getEditor().cancel();
            grid.getEditor().editItem(event.getItem());
            editDescriptionField.focus();
        });

        grid.getEditor().addSaveListener(this::onEditorSaveEvent);
        //Saving by click Enter
        grid.getElement().addEventListener("keydown", event -> grid.getEditor().save())
                .setFilter("event.key === 'Enter'");
    }

    private void activateLinkEditor() {
        if (editorAlreadyActivated) return; //to avoid double activation
        if (userLoggedIn) {
            editableLink = new EditableLink(appUtils.getShortDomain(), DeviceUtils.isMobileDevice());
            // Close the editor in case of backward between components
            editableLink.getElement()
                    .addEventListener("keydown",
                            event -> grid.getEditor().cancel())
                    .setFilter("event.key === 'Tab' && event.shiftKey");

            binder.forField(editableLink.getEditIdentField())
                    .withValidator(ident -> ident.length() >= 2, "Must contain at least 2 chars")
                    .withValidator(ident -> identValidator.validate(ident).ok(), "Back-part is not valid")
                    .withValidationStatusHandler(status -> {
                        editableLink.getEditIdentField().setInvalid(status.isError());
                        editableLink.getEditIdentField().setErrorMessage(status.getMessage().orElse(""));
                    })
                    .bind("ident");
            linkColumn.setEditorComponent(editableLink);
        }
        editorAlreadyActivated = true;
    }

    private boolean filterItems(final LinkInfo linkInfo) {
        String searchTerm = gridFilterField.getValue().trim();
        if (StringUtils.isBlank(searchTerm)) return true;

        boolean matchesIdent = matchesTerm(linkInfo.getIdent(), searchTerm);
        boolean matchesDescription = matchesTerm(linkInfo.getDescription(), searchTerm);

        String longLink = getLongLink(linkInfo);
        if (StringUtils.isBlank(longLink)) {
            return matchesIdent || matchesDescription;
        } else {
            boolean matchesLongLink = matchesTerm(longLink, searchTerm);
            return matchesIdent || matchesDescription || matchesLongLink;
        }
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
        EventBus.getDefault().register(this);

        updateDataAndState();
    }

    @Override
    protected void onDetach(final DetachEvent detachEvent) {
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

    /**
     * Triggers actions when {@link LinkInfoUpdatedEvent} received.
     * @param event event object with modified data inside
     */
    @Subscribe
    public void onLinkInfoUpdatedEvent(final LinkInfoUpdatedEvent event) {
        log.trace("{} {} received: {}", TAG, LinkInfoUpdatedEvent.class.getSimpleName(), event);
        onLinkInfoEvent(event.getLinkInfo());
    }

    private void onLinkEvent(final Link link) {
        if (userLoggedIn) {
            //act only if link is owned by current user.
            Optional<LinkInfo> linkInfo = linkInfoService.getLinkInfoByLink(link);
            if (linkInfo.isPresent() && linkInfo.get().getOwner().equals(user)) {
                updateDataAndState();
            }
        } else {
            //act only if link deleted or saved within our session
            if (sessionId.equals(Session.EMPTY_ID)) return;
            if (getSessionIdFromLink(link).equals(sessionId)) {
                updateDataAndState();
            }
        }
    }

    private void onLinkInfoEvent(final LinkInfo linkInfo) {
        //act only if link info within our session or user
        boolean isApplicable;
        if (this.userLoggedIn) {
            isApplicable = linkInfo.getOwner().equals(this.user);
        } else {
            isApplicable = linkInfo.getSession().equals(sessionId);
        }
        if (isApplicable) {
            updateDataAndState();
        }
    }


    private void onShareButtonClick(final LinkInfo item) {
        if (item != null) {
            String shortLink = appUtils.getShortUrl() + "/" + item.getIdent();

            if (ui != null && ui.getPage() != null && DeviceUtils.isMobileDevice()) {
                MobileShareMenu.createForPage(ui.getPage()).setLink(shortLink).show();
            } else {
                shareMenu.setShortLink(shortLink);
                if (StringUtils.isNotBlank(item.getDescription())) {
                    shareMenu.setDescription(item.getDescription());
                }
                shareMenu.show();
            }
        }
    }

    private void onEditButtonClick(final LinkInfo item) {
        if (item != null) {
            if (grid.getEditor().isOpen()) grid.getEditor().cancel();
            grid.getEditor().editItem(item);
        }
    }

    private void onDeleteButtonClick(final LinkInfo item) {
        if (item != null) {
            DeleteConfirmationDialog.create().setDeleteButtonAction(() -> this.deleteLinkAndLinkInfo(item)).show();
        }
    }

    private void onSaveButtonClick(final LinkInfo item) {
        if (item != null) {
            boolean identUpdated = isIdentUpdated(item);
            boolean editorHasValue = editableLink != null && StringUtils.isNotBlank(editableLink.getValue());
            if (identUpdated && editorHasValue && linkInfoService.linkInfoExistsForIdent(editableLink.getValue())) {
                final String errorMessage = "Already exists";
                editableLink.getEditIdentField().setInvalid(true);
                editableLink.getEditIdentField().setErrorMessage(errorMessage);
                return;
            }
            grid.getEditor().save();
        }
    }

    private void onCancelButtonClick(final LinkInfo item) {
        if (item != null) {
            grid.getEditor().cancel();
        }
    }

    private void updateDataAndState() {
        if (ui != null) {
            ui.access(() -> {
                updateData();
                updateState();
            });
        }
    }

    private void updateData() {
        final List<LinkInfo> records = new ArrayList<>();
        if (userLoggedIn) {
            log.debug("{} updating grid. Current User: {}", TAG, user);
            records.addAll(linkInfoService.getAllRecordsOwnedByUser(user));
        } else if (!sessionId.equals(Session.EMPTY_ID)) {
            log.debug("{} updating grid. Current session ID: {}", TAG, sessionId);
            records.addAll(linkInfoService.getAllRecordWithSession(sessionId));
        }

        GridListDataView<LinkInfo> dataView = grid.setItems(records);
        dataView.addFilter(this::filterItems);
    }

    private void updateState() {
        noRecordsBanner.setVisible(gridIsEmpty());
    }

    private void onEditorSaveEvent(final EditorSaveEvent<LinkInfo> event) {
        LinkInfo linkInfo = event.getItem();
        if (linkInfo == null) return;
        boolean updateSucceeded = updateLinkInfo(linkInfo);
        if (updateSucceeded) {
            this.savedNotification.open();
        }
    }

    private boolean updateLinkInfo(final LinkInfo linkInfo) {
        Optional<LinkInfo> oldLinkInfo = linkInfoService.getLinkInfoById(linkInfo.getId());
        if (oldLinkInfo.isPresent()) {
            boolean identNotUpdated = linkInfo.getIdent().equals(oldLinkInfo.get().getIdent());
            boolean descriptionNotUpdated =
                    StringUtils.isAllBlank(linkInfo.getDescription(), oldLinkInfo.get().getDescription())
                            || linkInfo.getDescription().equals(oldLinkInfo.get().getDescription());
            if (identNotUpdated && descriptionNotUpdated) return false; //just skipping with no message
            if (identNotUpdated) {
                linkInfoService.update(linkInfo);
            } else {
                if (userLoggedIn) {
                    OperationResult getLinkResult = linkService.getLinkByIdent(oldLinkInfo.get().getIdent());
                    if (getLinkResult.ok()) {
                        Link link = getLinkResult.getPayload(Link.class);
                        link.setIdent(linkInfo.getIdent());
                        OperationResult linkUpdateResult = linkService.updateLink(link);
                        if (linkUpdateResult.ok()) {
                            linkInfoService.update(linkInfo);
                        } else {
                            if (linkUpdateResult.getMessage() == null) {
                                String errorMessage = switch (linkUpdateResult.getResult()) {
                                    case OperationResult.CONFLICT ->
                                            "We already have link with given ident. Please try another one";
                                    case OperationResult.SYSTEM_DOWN ->
                                            "Failed to update data. System is partly inaccessible. "
                                                    + "Try again later";
                                    default -> "Something unexpected happened at server-side. Try again later.";
                                };
                                ErrorUtils.getErrorNotification(errorMessage).open();
                            } else {
                                ErrorUtils.getErrorNotification(linkUpdateResult.getMessage()).open();
                            }
                            return false;
                        }
                    } else {
                        ErrorUtils.getErrorNotification("Failed to update back-part").open();
                        return false;
                    }
                } else {
                    ErrorUtils.getErrorNotification("Back-part updates are allowed only for users. "
                            + "Become user once we introduce them").open();
                    return false;
                }
            }
        } else {
            ErrorUtils.getErrorNotification("Not saved. Internal error: ID mismatch").open();
            return false;
        }
        updateDataAndState();
        return true;
    }

    private void deleteLinkAndLinkInfo(final LinkInfo linkInfo) {
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
            case OperationResult.ELEMENT_NOT_FOUND ->
                    ErrorUtils.getErrorNotification("Deletion failed: no such link found").open();
            case OperationResult.SYSTEM_DOWN ->
                    ErrorUtils.getErrorNotification("System is partly inaccessible. Try again later").open();
            default -> ErrorUtils.getErrorNotification("Something wrong at server-side. Try again later").open();
        }
    }

    private void onQRCodeClicked(final ClickEvent<Image> imageClickEvent) {
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

    private Optional<Image> getBigQRCode(final ClickEvent<Image> imageClickEvent) {
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

    private String getTime(final Timestamp ts) {
        ZoneId timeZone;
        if (deviceUtils != null && deviceUtils.areDetailsSet()) {
            timeZone = ZoneId.of(deviceUtils.getTimezoneId());
        } else {
            timeZone = ZoneId.systemDefault();
        }
        ZonedDateTime tzDate = ts.toLocalDateTime().atZone(timeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Axe.C.TIME_DATE_FORMAT);
        return tzDate.format(formatter);
    }

    private String getSessionIdFromLink(final Link linkFromEvent) {
        Optional<LinkInfo> linkInfo = linkInfoService.getLinkInfoByLink(linkFromEvent);
        if (linkInfo.isPresent()) {
            return StringUtils.isNotBlank(linkInfo.get().getSession()) ? linkInfo.get().getSession() : Session.EMPTY_ID;
        } else {
            return Session.EMPTY_ID;
        }
    }

    private boolean gridIsEmpty() {
        return grid.getDataProvider().size(new Query<>()) == 0;
    }

    private boolean matchesTerm(final String value, final String searchTerm) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(searchTerm)) return false;
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean isSmallScreen() {
        boolean isSmallScreen = false;
        if (deviceUtils != null && deviceUtils.areDetailsSet()) {
            if (deviceUtils.isExtraSmallDevice()) {
                isSmallScreen = true;
            }
        } else {
            //fallback to user agent detection
            isSmallScreen = AxeSession.getCurrent().map(session -> session.getDevice().isMobile()).orElse(false);
        }
        return isSmallScreen;
    }

    private Notification createSavedNotification() {
        Notification notification = new Notification();
        notification.setText("Saved!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(Axe.Defaults.NOTIFICATION_DURATION_MILLIS);
        return notification;
    }

    private boolean isIdentUpdated(LinkInfo item) {
        boolean identUpdated = false;
        Optional<LinkInfo> oldLinkInfo = linkInfoService.getLinkInfoById(item.getId());
        if (oldLinkInfo.isPresent() && editableLink != null) {
            identUpdated = !Objects.equals(editableLink.getValue(), oldLinkInfo.get().getIdent());
        }
        return identUpdated;
    }

    public static class IDs {
        public static final String SESSION_BANNER = "sessionBanner";

        public static final String NO_RECORDS_BANNER = "noRecordsBanner";
        public static final String NO_RECORDS_BANNER_TEXT = "noRecordsBannerText";
        public static final String NO_RECORDS_BANNER_LINK = "noRecordsBannerLink";

        public static final String GRID = "grid";
        public static final String LINK_COLUMN_CLASS = "linkCol";
        public static final String DESCRIPTION_COLUMN_CLASS = "descriptionCol";
        public static final String QR_CODE_COLUMN_CLASS = "qrCodeCol";
        public static final String ITEM_DETAILS_CLASS = "item-details";
        public static final String ITEM_DETAILS_LINK_CLASS = "long-link";
        public static final String ITEM_DETAILS_CREATED_TIME_LABEL_CLASS = "created-time-label";
        public static final String ITEM_DETAILS_CREATED_TIME_CLASS = "created-time";
        public static final String ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS = "updated-time-label";
        public static final String ITEM_DETAILS_UPDATED_TIME_CLASS = "updated-time";
    }
}
