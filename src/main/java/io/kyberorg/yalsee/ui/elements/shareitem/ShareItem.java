package io.kyberorg.yalsee.ui.elements.shareitem;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.kyberorg.yalsee.ui.elements.ShareMenu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Common functionality for all items in {@link ShareMenu}.
 */
@Slf4j
public abstract class ShareItem extends Composite<VerticalLayout> {
    static final String DEFAULT_SHORT_LINK = "https://yals.ee";
    protected static final String DEFAULT_DESCRIPTION = "Yalsee: Yet another link shortener";

    private final Image logo;
    private final Text label;
    @Setter(value = AccessLevel.PROTECTED)
    private String fullLink;
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

    /**
     * Creates and sets {@link #fullLink} using {@link #setFullLink(String)}.
     */
    protected abstract void constructLink();

    protected void onLogoClick(final ClickEvent<Image> clickEvent) {
        UI ui = null;
        if (getUI().isPresent()) {
            ui = getUI().get();
        } else if (UI.getCurrent() != null) {
            ui = UI.getCurrent();
        }

        if (ui != null) {
            ui.getPage().open(fullLink, "_blank");
        } else {
            log.warn("Failed to open URL. No UI found.");
        }
    }
}