package io.kyberorg.yalsee.ui.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * {@link Dialog} that allows share links.
 */
public class ShareMenu extends Composite<Dialog> {
    private final H4 title = new H4("Share");
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
        VerticalLayout dialogLayout = createDialogLayout();
        setClassNames();
        dialog.add(dialogLayout);
    }

    private void setClassNames() {
        title.setClassName("share-menu-title");
        closeIcon.setClassName("share-menu-close-icon");
    }

    private VerticalLayout createDialogLayout() {
        closeIcon.addClickListener(e -> dialog.close());

        HorizontalLayout titleAndCloseIconLayout = new HorizontalLayout();
        titleAndCloseIconLayout.add(title, closeIcon);

        VerticalLayout dialogLayout = new VerticalLayout(titleAndCloseIconLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.setClassName("share-menu");

        return dialogLayout;
    }
}
