package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import org.apache.commons.lang3.StringUtils;

public class EmailShareItem extends ShareItem {
    public EmailShareItem() {
        setImageFile(ShareMenu.Icons.EMAIL);
        setLabelText("Email");
    }

    @Override
    public void constructLink() {
        StringBuilder sb = new StringBuilder("emailto: ");

        sb.append("body=").append(getShortLink());
        boolean isDefaultShortLink = getShortLink().equals(DEFAULT_SHORT_LINK);
        boolean descriptionNotEmpty = StringUtils.isNotBlank(getDescription());
        if (isDefaultShortLink || descriptionNotEmpty) {
            sb.append("-");
            sb.append(getDescription());
        }
        fullLink = sb.toString(); //TODO encode URL
    }

}
