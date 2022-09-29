package io.kyberorg.yalsee.ui.elements.shareitem;

import com.vaadin.flow.component.page.Page;
import org.apache.commons.lang3.StringUtils;

public class MobileShareMenu {
    private final Page page;
    private String shortLink;

    public static MobileShareMenu createForPage(final Page page) throws IllegalArgumentException {
        if (page == null) throw new IllegalArgumentException("Page cannot be null");
        return new MobileShareMenu(page);
    }

    private MobileShareMenu(final Page page) {
        this.page = page;
    }

    public MobileShareMenu setLink(String link) {
        if (StringUtils.isBlank(link)) {
            link = ShareItem.DEFAULT_SHORT_LINK;
        }
        this.shortLink = link;
        return this;
    }

    public void show() {
        page.executeJs("window.openShareMenu($0)", shortLink);
    }
}
