package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import org.apache.commons.lang3.StringUtils;

public class TwitterShareItem extends ShareItem {
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
        fullLink = sb.toString();
    }

}
