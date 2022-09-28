package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import io.kyberorg.yalsee.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;

public class EmailShareItem extends ShareItem {
    public EmailShareItem() {
        setImageFile(ShareMenu.Icons.EMAIL);
        setLabelText("Email");
    }

    @Override
    public void constructLink() {
        boolean isDefaultShortLink = getShortLink().equals(DEFAULT_SHORT_LINK);
        boolean descriptionNotEmpty = StringUtils.isNotBlank(getDescription());

        StringBuilder subjectBuilder = new StringBuilder();
        if (!isDefaultShortLink || descriptionNotEmpty) {
            subjectBuilder.append(getDescription());
        } else {
            subjectBuilder.append(getShortLink());
        }

        StringBuilder bodyBuilder = new StringBuilder(getShortLink());
        if (!isDefaultShortLink || descriptionNotEmpty) {
            bodyBuilder.append(" - ");
            bodyBuilder.append(getDescription());
        }

        String subject = UrlUtils.encodeUrl(subjectBuilder.toString());
        String body = UrlUtils.encodeUrl(bodyBuilder.toString());

        fullLink = "mailto:" + "subject=" + subject + "&body=" + body;
    }
}
