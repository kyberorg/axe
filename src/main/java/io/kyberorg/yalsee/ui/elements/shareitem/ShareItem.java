package io.kyberorg.yalsee.ui.elements.shareitem;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.AccessLevel;
import lombok.Setter;

public abstract class ShareItem extends Composite<VerticalLayout> {
    protected Image logo;
    protected Text label;
    @Setter(value = AccessLevel.PROTECTED)
    protected String fullLink;

    protected ShareItem() {
        logo = new Image();
        logo.setSrc("/images/logo.png");
        logo.setClassName("share-item-logo");
        logo.addClickListener(this::onLogoClick);

        label = new Text("ShareItem");
        getContent().add(logo, label);
    }

    protected void setImageFile(final String imageFile) {
        logo.setSrc("/images/" + imageFile); //TODO path
    }

    protected void setLabelText(final String labelText) {
        label.setText(labelText);
    }

    protected abstract void constructLink(final String shortLink, final String description);

    protected void onLogoClick(final ClickEvent<Image> clickEvent) {
        Notification.show("Opening " + fullLink); //TODO replace with opening link in new tab.
    }
}
