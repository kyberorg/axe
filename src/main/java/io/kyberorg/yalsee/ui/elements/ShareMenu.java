package io.kyberorg.yalsee.ui.elements;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import io.kyberorg.yalsee.ui.elements.shareitem.EmailShareItem;
import io.kyberorg.yalsee.ui.elements.shareitem.FacebookShareItem;
import io.kyberorg.yalsee.ui.elements.shareitem.ShareItem;
import io.kyberorg.yalsee.ui.elements.shareitem.TwitterShareItem;
import io.kyberorg.yalsee.utils.ClipboardUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Dialog} that allows share links.
 */
public final class ShareMenu extends Composite<Dialog> {
    private final Icon closeIcon = VaadinIcon.CLOSE.create();
    private final Dialog dialog = getContent();
    private final List<ShareItem> shareItems = new ArrayList<>();
    private TextField shortLinkText;

    /**
     * Creates dialog. Created dialog ain't open automatically.
     *
     * @return created {@link ShareMenu}.
     */
    public static ShareMenu create() {
        return new ShareMenu();
    }

    /**
     * Sets short link for all {@link ShareItem}s.
     *
     * @param shortLink string with valid short link.
     */
    public void setShortLink(final String shortLink) {
        shortLinkText.setValue(shortLink);
        shareItems.forEach(item -> item.updateShortLink(shortLink));
    }

    /**
     * Sets description for all {@link ShareItem}s.
     *
     * @param description not empty string with description.
     */
    public void setDescription(final String description) {
        shareItems.forEach(item -> item.updateDescription(description));
    }

    /**
     * Displays dialog.
     */
    public void show() {
        this.dialog.open();
    }

    private ShareMenu() {
        createDialogHeader();
        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);
    }

    private void createDialogHeader() {
        dialog.setHeaderTitle("Share your link");
        closeIcon.addClickListener(e -> dialog.close());
        dialog.getHeader().add(closeIcon);
    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout dialogLayout = new VerticalLayout();

        Scroller scroller = new Scroller();
        scroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        scroller.setWidthFull();

        HorizontalLayout locationsLayout = new HorizontalLayout();
        locationsLayout.setClassName("share-items-layout");

        shareItems.add(new EmailShareItem());
        shareItems.add(new FacebookShareItem());
        shareItems.add(new TwitterShareItem());

        shareItems.forEach(locationsLayout::add);

        HorizontalLayout textAndCopyLayout;
        textAndCopyLayout = new HorizontalLayout();
        textAndCopyLayout.setWidthFull();

        shortLinkText = new TextField();
        shortLinkText.setValue("https://yals.ee");
        shortLinkText.setReadOnly(true);
        shortLinkText.setWidthFull();

        Button copyButton = new Button();
        copyButton.setText("Copy");
        copyButton.addClickListener(this::onCopyButtonClicked);

        scroller.setContent(locationsLayout);
        textAndCopyLayout.add(shortLinkText, copyButton);
        dialogLayout.add(scroller, textAndCopyLayout);
        return dialogLayout;
    }

    private void onCopyButtonClicked(final ClickEvent<Button> event) {
        ClipboardUtils.copyToClipboardAndNotify(shortLinkText.getValue(),
                "Short link copied", Notification.Position.MIDDLE);
    }

    public enum Icons {
        EMAIL("email.png"),
        FACEBOOK("facebook.png"),
        TWITTER("twitter.png");

        @Getter
        private final String fileName;

        Icons(final String fileName) {
            this.fileName = fileName;
        }
    }
}