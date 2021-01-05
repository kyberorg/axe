package eu.yals.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import eu.yals.ui.MainView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for element at {@link eu.yals.ui.MainView}
 *
 * @since 2.8
 */
public class MainViewPageObject {

    public static final SelenideElement LOGO = $("#" + MainView.IDs.APP_LOGO);
}
