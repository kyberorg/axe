package io.kyberorg.yalsee.ui.pages.home;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import io.kyberorg.yalsee.ui.elements.shareitem.EmailShareItem;

/**
 * {@link Dialog} that allows share links.
 */
public final class ShareMenu extends Composite<Dialog> {
    private final Icon closeIcon = VaadinIcon.CLOSE.create();

    private final Dialog dialog = getContent();

    /**
     * Creates dialog. Created dialog ain't open automatically.
     *
     * @return created {@link ShareMenu}.
     */
    public static ShareMenu create() {
        return new ShareMenu();
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

        HorizontalLayout locationsLayout = new HorizontalLayout();
        EmailShareItem emailShareItem = new EmailShareItem();
        emailShareItem.constructLink("shortLinkHere", "descHere"); //TODO maybe should go to constructor
        EmailShareItem emailShareItem1 = new EmailShareItem();
        emailShareItem.constructLink("shortLinkHere", "descHere");

        locationsLayout.add(emailShareItem, emailShareItem1);

        HorizontalLayout textAndCopyLayout = new HorizontalLayout();
        TextField tx = new TextField();
        tx.setValue("shortLinkHere");
        tx.setReadOnly(true);

        Button copyButton = new Button();
        copyButton.setText("Copy");
        copyButton.addClickListener(this::onCopyButtonClicked);

        textAndCopyLayout.add(tx, copyButton);
        dialogLayout.add(locationsLayout, textAndCopyLayout);
        return dialogLayout;
    }

    private void onCopyButtonClicked(final ClickEvent<Button> event) {
        Notification.show("Copied");
    }
}