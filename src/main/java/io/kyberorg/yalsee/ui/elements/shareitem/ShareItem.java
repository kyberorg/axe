package io.kyberorg.yalsee.ui.elements.shareitem;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.kyberorg.yalsee.exception.URLEncodeException;
import io.kyberorg.yalsee.ui.elements.ShareMenu;
import io.kyberorg.yalsee.utils.DeviceUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ShareItem extends Composite<VerticalLayout> {
    protected static final String DEFAULT_SHORT_LINK = "https://yals.ee";
    protected static final String DEFAULT_DESCRIPTION = "Yalsee: Yet another link shortener";

    protected Image logo;
    protected Text label;
    @Setter(value = AccessLevel.PROTECTED)
    protected String fullLink;
    @Getter(value = AccessLevel.PROTECTED)
    private String shortLink = DEFAULT_SHORT_LINK;
    @Getter(value = AccessLevel.PROTECTED)
    private String description = DEFAULT_DESCRIPTION;

    public void updateShortLink(final String newShortLink) {
        this.shortLink = newShortLink;
        this.description = "";
        constructLink();
    }

    public void updateDescription(final String newDescription) {
        this.description = newDescription;
        constructLink();
    }

    protected ShareItem() {
        logo = new Image();
        logo.setSrc("/images/logo.png");
        logo.setClassName("share-item-logo");
        logo.addClickListener(this::onLogoClick);

        label = new Text("ShareItem");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(logo, label);
    }

    protected void setImageFile(final ShareMenu.Icons icon) {
        logo.setSrc("/images/" + icon.getFileName());
    }

    protected void setLabelText(final String labelText) {
        label.setText(labelText);
    }

    protected abstract void constructLink();

    protected void onLogoClick(final ClickEvent<Image> clickEvent) {
        String encodedUrl;
        try {
            encodedUrl = fullLink; //TODO fixit
            Notification.show(encodedUrl); //TODO remote after debug
            //encodedUrl = UrlUtils.encodeUrl(fullLink);
            UI ui = null;
            if (getUI().isPresent()) {
                ui = getUI().get();
            } else if (UI.getCurrent() != null) {
                ui = UI.getCurrent();
            }
            if (ui != null) {
                if (DeviceUtils.isMobileDevice()) {
                    ui.getPage().open(encodedUrl, "_parent");
                } else {
                    ui.getPage().open(encodedUrl, "_blank");
                }

            } else {
                log.warn("Failed to open URL. No UI found.");
            }

        } catch (URLEncodeException e) {
            log.warn("Failed to open URL. Encoding failed. Got {}" + URLEncodeException.class.getSimpleName());
            ErrorUtils.getErrorNotification("Internal Error: failed to open URL").open();
        }


    }
}
