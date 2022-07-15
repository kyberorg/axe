package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import org.apache.commons.lang3.StringUtils;

public class FacebookShareItem extends ShareItem {
    public FacebookShareItem() {
        setImageFile(ShareMenu.Icons.FACEBOOK);
        setLabelText("Facebook");
    }

    @Override
    public void constructLink() {
        StringBuilder sb = new StringBuilder("https://facebook.com/");

        sb.append("url=").append(getShortLink());
        boolean isDefaultShortLink = getShortLink().equals(DEFAULT_SHORT_LINK);
        boolean descriptionNotEmpty = StringUtils.isNotBlank(getDescription());
        if (isDefaultShortLink || descriptionNotEmpty) {
            sb.append("&");
            sb.append("description=").append(getDescription());
        }
        fullLink = sb.toString(); //TODO encode URL
    }

}
