package pm.axe.test.ui.mylinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static pm.axe.constants.App.THREE;

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
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.
    }

    /**
     * Two links with different Description. Filtering by one of them. One row should be shown.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void onTwoLinksWithDifferentDescription_filterShouldFilterOne() {
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", "Kyberorg");
        HomePageObject.saveLinkWithDescription("https://github.com", "GitHub");

        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(2));
        MyLinksViewPageObject.GridFilter.setFilter("GitHub");

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(1));
    }

    /**
     * Three Links. Two of them have word "Link" in Description. Filter "Link". Should filter out 1 row.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void onThreeLinks_twoOfThemHaveWordLink_filterLink_shouldFilterTwoLinks() {
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", "Link One");
        HomePageObject.saveLinkWithDescription("https://github.com", "Link Two");
        HomePageObject.saveLinkWithDescription("https://google.ee", "Google");

        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(THREE));

        MyLinksViewPageObject.GridFilter.setFilter("Link");

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(2));
    }

    /**
     * Filter filters case insensitively.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void filterFiltersCaseInsensitively() {
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/axe/issues/762", "GitHub");
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/axe/issues/762", "Github");
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/axe/issues/762", "github");

        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(THREE));

        MyLinksViewPageObject.GridFilter.setFilter("github");
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        MyLinksViewPageObject.GridFilter.cleanFilter();

        MyLinksViewPageObject.GridFilter.setFilter("Github");
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        MyLinksViewPageObject.GridFilter.cleanFilter();

        MyLinksViewPageObject.GridFilter.setFilter("GitHub");
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        MyLinksViewPageObject.GridFilter.cleanFilter();

        MyLinksViewPageObject.GridFilter.setFilter("GITHUB");
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(THREE));
        MyLinksViewPageObject.GridFilter.cleanFilter();
    }

    /**
     * Filtering when one item has not any description set, another has. Filtering by second description.
     * Row without description should be filtered out.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void whenOneLinkHasNullDescriptionAndOtherNot_filterFiltersSecondOne() {
        HomePageObject.saveLinkWithDescription("https://github.com/kyberorg/axe/issues/762", "GitHub");
        HomePageObject.saveOneLink("https://kyberorg.io");

        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(2));

        MyLinksViewPageObject.GridFilter.setFilter("GitHub");
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(1));
    }

    /**
     * Filter also filters by Long Link.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void whenBothLinkHaveNullDescription_filterFiltersByLongLink() {
        HomePageObject.saveOneLink("https://kyberorg.io");
        HomePageObject.saveOneLink("https://google.ee");

        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(2));

        MyLinksViewPageObject.GridFilter.setFilter("google");
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().filterBy(visible).shouldHave(size(1));
    }
}
