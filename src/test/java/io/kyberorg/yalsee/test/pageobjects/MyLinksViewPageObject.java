package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.GridElement;
import io.kyberorg.yalsee.ui.MyLinksView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object {@link MyLinksView}
 *
 * @since 3.2
 */
public class MyLinksViewPageObject {
    private MyLinksViewPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final GridElement gridElement = GridElement.byCss("vaadin-grid#" + MyLinksView.IDs.GRID);

    public static final SelenideElement PAGE = $("#" + MyLinksView.class.getSimpleName());

    public static class Banners {
        public static final SelenideElement SESSION_BANNER = $("#" + MyLinksView.IDs.SESSION_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER_TEXT = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER_TEXT);
        public static final SelenideElement NO_RECORDS_BANNER_LINK = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER_LINK);
    }

    public static class Grid {
        public static final SelenideElement GRID = $("vaadin-grid#" + MyLinksView.IDs.GRID);

        public static class Header {
            public static final SelenideElement ROW = gridElement.getHeader();
            public static final ElementsCollection CELLS = gridElement.getHeadersCells();
            public static final SelenideElement LINK_CELL = gridElement.getCell(0, 0);
            public static final SelenideElement DESCRIPTION_CELL = gridElement.getCell(0, 1);
            public static final SelenideElement QR_CODE_CELL = gridElement.getCell(0, 2);
            public static final SelenideElement ACTIONS_CELL = gridElement.getCell(0, 3);
        }

        public static class Data {
            public static final ElementsCollection ROWS = gridElement.getDataRows();
        }
    }
}
