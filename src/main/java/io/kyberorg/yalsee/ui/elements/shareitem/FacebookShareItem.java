package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import io.kyberorg.yalsee.utils.AppUtils;
import org.apache.commons.lang3.StringUtils;

public class FacebookShareItem extends ShareItem {
    public FacebookShareItem() {
        setImageFile(ShareMenu.Icons.FACEBOOK);
        setLabelText("Facebook");
    }

    @Override
    public void constructLink() {
        StringBuilder sb = new StringBuilder("https://facebook.com/dialog/share?");
        sb.append("app_id=").append(AppUtils.getFacebookId());
        sb.append("&");
        sb.append("href=").append(getShortLink());
        boolean isDefaultShortLink = getShortLink().equals(DEFAULT_SHORT_LINK);
        boolean descriptionNotEmpty = StringUtils.isNotBlank(getDescription());
        if (isDefaultShortLink || descriptionNotEmpty) {
            sb.append("&");
            sb.append("description=").append(getDescription());
        }
        sb.append("&display=popup");
        fullLink = sb.toString();
    }

}
