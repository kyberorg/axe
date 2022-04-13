package io.kyberorg.yalsee.test.pageobjects.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page <a href="https://vr.fi">https://vr.fi</a> .
 *
 * @since 2.2
 */
@SuppressWarnings("SpellCheckingInspection") //this is Finnish lang
public final class VR {
    public static final String TITLE_TEXT = "Uudistunut vr.fi – tervetuloa yhteiselle matkalle - VR";
    public static final SelenideElement VR_LOGO = $("svg[data-testid='VRLogoIcon']");

    private VR() {
        throw new UnsupportedOperationException("Utility class");
    }
}
