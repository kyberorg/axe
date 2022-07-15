package io.kyberorg.yalsee.ui.elements.shareitem;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.kyberorg.yalsee.exception.URLEncodeException;
import io.kyberorg.yalsee.ui.elements.ShareMenu;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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
            //encodedUrl = UrlUtils.encodeUrl(fullLink);
            getUI().ifPresent(ui -> ui.getPage().open(encodedUrl, "_blank"));
        } catch (URLEncodeException e) {
            ErrorUtils.getErrorNotification("Internal Error:  failed to open URL").open();
        }


    }
}
