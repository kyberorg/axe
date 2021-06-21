package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

import static com.codeborne.selenide.Selenide.$;

/**
 * Common Application elements. See {@link YalseeLayout}.
 *
 * @since 3.0.7
 */
public class YalseeCommonsPageObject {
    public static final SelenideElement MAIN_AREA = $(".main-area");
}
