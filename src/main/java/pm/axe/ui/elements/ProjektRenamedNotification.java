package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import pm.axe.Endpoint;
import pm.axe.session.AxeSession;

/**
 * Yalsee is now Axe Notification.
 */
public class ProjektRenamedNotification extends Composite<Notification> {

    /**
     * Creates Notification.
     *
     * @return created {@link ProjektRenamedNotification}.
     */
    public static ProjektRenamedNotification create() {
        return new ProjektRenamedNotification();
    }

    /**
     * Creates new Notification.
     */
    public ProjektRenamedNotification() {
        Notification notification = getContent();
        notification.setPosition(Notification.Position.TOP_CENTER);

        Shortcuts.addShortcutListener(notification, notification::close, Key.ESCAPE);

        Div text = new Div(new Text("Yalsee is now Axe."));

        Anchor readMoreLink = new Anchor("/" + Endpoint.UI.MEET_AXE_PAGE, "Read more.");
        readMoreLink.getElement().addEventListener("click", e -> this.close());

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.getStyle().set("margin-right", "0.5rem");
        closeButton.addClickListener(e -> this.close());

        HorizontalLayout layout = new HorizontalLayout(text, readMoreLink, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.AUTO);
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        notification.add(layout);
    }

    /**
     * Opens Notification.
     */
    public void show() {
        getContent().open();
    }

    private void close() {
        AxeSession.getCurrent().ifPresent(ys -> ys.getFlags().setRenameNotificationAlreadyShown(true));
        getContent().close();
    }
}
