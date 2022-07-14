package io.kyberorg.yalsee.ui.elements.shareitem;

public class EmailShareItem extends ShareItem {
    public EmailShareItem() {
        setImageFile("logo.png"); //TODO fix
        setLabelText("Email");
    }

    @Override
    public void constructLink(String shortLink, String description) {
        fullLink = "emailto: " + shortLink + " - " + description;
    }

}
