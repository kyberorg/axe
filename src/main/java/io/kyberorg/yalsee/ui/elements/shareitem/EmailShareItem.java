package io.kyberorg.yalsee.ui.elements.shareitem;

import org.apache.commons.lang3.StringUtils;

public class EmailShareItem extends ShareItem {
    public EmailShareItem() {
        setImageFile("logo.png"); //TODO fix
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
