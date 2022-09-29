package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import org.apache.commons.lang3.StringUtils;

/**
 * Share link via Twitter.
 */
public class TwitterShareItem extends ShareItem {
    /**
     * Creates {@link TwitterShareItem}.
     */
    public TwitterShareItem() {
        setImageFile(ShareMenu.Icons.TWITTER);
        setLabelText("Twitter");
    }

    @Override
    public void constructLink() {
        StringBuilder sb = new StringBuilder("https://twitter.com/intent/tweet?");

        sb.append("url=").append(getShortLink());
        boolean isDefaultShortLink = getShortLink().equals(DEFAULT_SHORT_LINK);
        boolean descriptionNotEmpty = StringUtils.isNotBlank(getDescription());
        if (isDefaultShortLink || descriptionNotEmpty) {
            sb.append("&");
            sb.append("text=").append(getDescription());
        }
        String fullLink = sb.toString();
        setFullLink(fullLink);
    }

}
