package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MainViewPageObject.GOOGLE_ANALYTICS_CONTROL_SPAN;

public class SeoTest extends SelenideTest {

    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();
        open("/");
        VaadinPageObject.waitForVaadin();
    }

    @Test
    public void correctGoogleAnalyticsScriptLoadedAndHidden() {
        GOOGLE_ANALYTICS_CONTROL_SPAN.should(exist);

        GOOGLE_ANALYTICS_CONTROL_SPAN.shouldNotBe(visible);
        GOOGLE_ANALYTICS_CONTROL_SPAN.shouldHave(attribute("aria-hidden","true"));

        //TODO value has hardcoded html name, change it with TestEnv enum/class jne, which provides per env based info
        GOOGLE_ANALYTICS_CONTROL_SPAN.shouldHave(attribute("aria-valuetext","gtag.dev.html"));
    }
}
