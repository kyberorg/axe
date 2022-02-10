package io.kyberorg.yalsee.test.ui.mylinks;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.GridFilter;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static io.kyberorg.yalsee.constants.App.THREE;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.openMyLinksPage;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Tests Filter for Grid.
 *
 * @since 3.10
 */
public class GridFilterTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        //session cleanup
        openMyLinksPage();
        CookieBannerPageObject.closeBannerIfAny();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        CookieBannerPageObject.closeBannerIfAny(); //banner re-appears for new session
    }

    /**
     * Two links with different Description. Filtering by one of them. One row should be shown.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void onTwoLinksWithDifferentDescription_filterShouldFilterOne() {
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", "Kyberorg");
        HomePageObject.saveLinkWithDescription("https://github.com", "GitHub");

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(2));
        GridFilter.setFilter("GitHub");

        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(1));
    }

    /**
     * Three Links. Two of them have word "Link" in Description. Filter "Link". Should filter out 1 row.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void onThreeLinks_twoOfThemHaveWordLink_filterLink_shouldFilterTwoLinks() {
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", "Link One");
        HomePageObject.saveLinkWithDescription("https://github.com", "Link Two");
        HomePageObject.saveLinkWithDescription("https://google.ee", "Google");

        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(THREE));

        GridFilter.setFilter("Link");

        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(2));
    }

    /**
     * Filter filters case insensitively.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void filterFiltersCaseInsensitively() {
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/yalsee/issues/762", "GitHub");
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/yalsee/issues/762", "Github");
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/yalsee/issues/762", "github");

        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(THREE));

        GridFilter.setFilter("github");
        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        GridFilter.cleanFilter();

        GridFilter.setFilter("Github");
        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        GridFilter.cleanFilter();

        GridFilter.setFilter("GitHub");
        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        GridFilter.cleanFilter();

        GridFilter.setFilter("GITHUB");
        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        GridFilter.cleanFilter();
    }

    /**
     * Filtering when one item has not any description set, another has. Filtering by second description.
     * Row without description should be filtered out.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void whenOneLinkHasNullDescriptionAndOtherNot_filterFiltersSecondOne() {
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/yalsee/issues/762", "GitHub");
        HomePageObject.saveOneLink("https://kyberorg.io");

        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(2));

        GridFilter.setFilter("GitHub");
        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(1));
    }

    /**
     * Filter also filters by Long Link.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void whenBothLinkHaveNullDescription_filterFiltersByLongLink() {
        HomePageObject.saveOneLink("https://kyberorg.io");
        HomePageObject.saveOneLink("https://google.ee");

        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(2));

        GridFilter.setFilter("google");
        Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(1));
    }
}
