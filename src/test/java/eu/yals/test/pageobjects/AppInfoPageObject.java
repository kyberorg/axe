package eu.yals.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import eu.yals.ui.dev.AppInfoView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link AppInfoView}.
 *
 * @since 2.7
 */
public class AppInfoPageObject {

    public static class PublicInfoArea {
        public static final SelenideElement PUBLIC_INFO_AREA = $("#" + AppInfoView.IDs.PUBLIC_INFO_AREA);
        public static final SelenideElement VERSION = $("#" + AppInfoView.IDs.VERSION);
        public static final SelenideElement COMMIT_LINK = $("#" + AppInfoView.IDs.COMMIT_LINK);
    }
}
