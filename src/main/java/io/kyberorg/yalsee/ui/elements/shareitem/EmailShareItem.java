package io.kyberorg.yalsee.ui.elements.shareitem;

import io.kyberorg.yalsee.ui.elements.ShareMenu;
import org.apache.commons.lang3.StringUtils;

/**
 * Share link via Email.
 */
public class EmailShareItem extends ShareItem {
    /**
     * Constructs {@link EmailShareItem}.
     */
    public EmailShareItem() {
        setImageFile(ShareMenu.Icons.EMAIL);
        setLabelText("Email");
    }

    @Override
    public void constructLink() {
        String subject;
        String body;

        boolean isDefaultShortLink = getShortLink().equals(DEFAULT_SHORT_LINK);
        boolean descriptionNotEmpty = StringUtils.isNotBlank(getDescription());

        if (isDefaultShortLink) {
            subject = DEFAULT_DESCRIPTION;
            body = DEFAULT_SHORT_LINK + " - " + DEFAULT_DESCRIPTION;
        } else {
            StringBuilder subjectBuilder = new StringBuilder();
            if (descriptionNotEmpty) {
                subjectBuilder.append(getDescription());
            }

            StringBuilder bodyBuilder = new StringBuilder(getShortLink());
            if (descriptionNotEmpty) {
                bodyBuilder.append(" - ");
                bodyBuilder.append(getDescription());
            }

            subject = subjectBuilder.toString();
            body = bodyBuilder.toString();
        }
        String fullLink = "mailto:?" + "subject=" + subject + "&body=" + body;
        setFullLink(fullLink);
    }
}
