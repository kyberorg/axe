package pm.axe.ui.elements;

import com.vaadin.flow.component.page.Page;
import pm.axe.ui.elements.shareitem.ShareItem;
import org.apache.commons.lang3.StringUtils;

/**
 * Native Share Menu for Mobile Phones.
 */
public final class MobileShareMenu {
    private final Page page;
    private String shortLink;

    /**
     * Creates Mobile Share Menu from given Page.
     *
     * @param page Vaadin's {@link Page} object to execute JavaScript at.
     * @return created {@link MobileShareMenu} with {@link Page} set.
     * @throws IllegalArgumentException when {@link Page} object is {@code null}.
     */
    public static MobileShareMenu createForPage(final Page page) throws IllegalArgumentException {
        if (page == null) throw new IllegalArgumentException("Page cannot be null");
        return new MobileShareMenu(page);
    }

    private MobileShareMenu(final Page page) {
        this.page = page;
    }

    /**
     * Set Short Link.
     *
     * @param link not-empty string with short link.
     * @return same object, but with {@link #shortLink} set.
     */
    public MobileShareMenu setLink(final String link) {
        this.shortLink = StringUtils.isNotBlank(link) ? link : ShareItem.getDefaultShortLink();
        return this;
    }

    /**
     * Displays native share menu.
     */
    public void show() {
        page.executeJs("window.openShareMenu($0)", shortLink);
    }
}
