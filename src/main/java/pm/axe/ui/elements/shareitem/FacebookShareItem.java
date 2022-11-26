package pm.axe.ui.elements.shareitem;

import org.apache.commons.lang3.StringUtils;
import pm.axe.ui.elements.ShareMenu;
import pm.axe.utils.AppUtils;

/**
 * Share link via Facebook.
 */
public class FacebookShareItem extends ShareItem {
    /**
     * Creates {@link FacebookShareItem}.
     */
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
        String fullLink = sb.toString();
        setFullLink(fullLink);
    }

}
