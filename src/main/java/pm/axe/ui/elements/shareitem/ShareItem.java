package pm.axe.ui.elements.shareitem;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pm.axe.ui.elements.ShareMenu;

/**
 * Common functionality for all items in {@link ShareMenu}.
 */
@Slf4j
public abstract class ShareItem extends Composite<VerticalLayout> {
    protected static final String DEFAULT_SHORT_LINK = "https://axe.pm";
    protected static final String DEFAULT_DESCRIPTION = "Axe.pm - Short Links for free";

    private final Image logo;
    private final Text label;
    @Setter(value = AccessLevel.PROTECTED)
    private String fullLink;
    @Getter(value = AccessLevel.PROTECTED)
    private String shortLink = DEFAULT_SHORT_LINK;
    @Getter(value = AccessLevel.PROTECTED)
    private String description = DEFAULT_DESCRIPTION;

    /**
     * Provides {@link #DEFAULT_SHORT_LINK} to classes that are not {@link ShareItem}.
     *
     * @return string with {@link #DEFAULT_SHORT_LINK}
     */
    public static String getDefaultShortLink() {
        return DEFAULT_SHORT_LINK;
    }

    /**
     * Updates short link.
     *
     * @param newShortLink not-empty string with short link.
     */
    public void updateShortLink(final String newShortLink) {
        this.shortLink = newShortLink;
        this.description = "";
        constructLink();
    }

    /**
     * Updates description.
     *
     * @param newDescription not-empty string with description
     */
    public void updateDescription(final String newDescription) {
        this.description = newDescription;
        constructLink();
    }

    /**
     * Creates {@link ShareItem}.
     */
    protected ShareItem() {
        logo = new Image();
        logo.setSrc("/images/logo.png");
        logo.setClassName("share-item-logo");
        logo.addClickListener(this::onLogoClick);

        label = new Text("ShareItem");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(logo, label);
    }

    /**
     * Sets Item's Icon.
     *
     * @param icon icon from selections.
     */
    protected void setImageFile(final ShareMenu.Icons icon) {
        logo.setSrc("/images/" + icon.getFileName());
    }

    /**
     * Item's Label aka Name.
     *
     * @param labelText non-empty string with item's label or name.
     */
    protected void setLabelText(final String labelText) {
        label.setText(labelText);
    }

    /**
     * Creates and sets {@link #fullLink} using {@link #setFullLink(String)}.
     */
    protected abstract void constructLink();

    /**
     * Action that should happen when user clicks on icon.
     *
     * @param clickEvent Vaadin's {@link ClickEvent}, populated by Vaadin itself.
     */
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
