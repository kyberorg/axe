package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.GridElement;
import io.kyberorg.yalsee.ui.MyLinksView;
import lombok.Data;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object {@link MyLinksView}
 *
 * @since 3.2
 */
public class MyLinksViewPageObject {
    private static final GridElement gridElement = GridElement.byCss("vaadin-grid#" + MyLinksView.IDs.GRID);
    private static Grid grid = null;
    public static final SelenideElement PAGE = $("#" + MyLinksView.class.getSimpleName());

    public static class Banners {
        public static final SelenideElement SESSION_BANNER = $("#" + MyLinksView.IDs.SESSION_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER_TEXT = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER_TEXT);
        public static final SelenideElement NO_RECORDS_BANNER_LINK = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER_LINK);
    }

    public Grid getGrid() {
        if (grid == null) {
            grid = new Grid();
        }
        return grid;
    }

    @Data
    public static class Grid {
        private final SelenideElement selfElement = $("vaadin-grid#" + MyLinksView.IDs.GRID);

        private Header header = new Header();
        private GridData gridData = new GridData();

        @Data
        public static class Header {
            private final SelenideElement row = gridElement.getHeader();
            private final ElementsCollection cells = gridElement.getHeadersCells();
            private final SelenideElement linkCell = gridElement.getCell(0, 0);
            private final SelenideElement descriptionCell = gridElement.getCell(0, 1);
            private final SelenideElement qrCodeCell = gridElement.getCell(0, 2);
            private final SelenideElement actionCell = gridElement.getCell(0, 3);
        }

        @Data
        public static class GridData {
            private final ElementsCollection dataRows = gridElement.getDataRows();

            public Row getRow(final int rowNumber) {
                return new Row(rowNumber);
            }

            @Data
            public class Row {
                private final int rowNumber;

                public Row(final int rowNumber) {
                    if (rowNumber > 0) {
                        this.rowNumber = rowNumber;
                    } else {
                        throw new IllegalArgumentException("Row Number must be positive number");
                    }
                }

                public SelenideElement getLinkCell() {
                    return gridElement.getCell(this.rowNumber, 0);
                }

                public SelenideElement getDescriptionCell() {
                    return gridElement.getCell(this.rowNumber, 1);
                }

                public SelenideElement getQRCodeCell() {
                    return gridElement.getCell(this.rowNumber, 2);
                }

                public SelenideElement getActionsCell() {
                    return gridElement.getCell(this.rowNumber, 3);
                }
            }
        }
    }

    public static final SelenideElement BIG_QR_CODE_MODAL = $("vaadin-dialog-overlay");

    public static void cleanSession() {
        grid.getSelfElement().sendKeys(Keys.chord(Keys.SHIFT, "q"));
    }

    public static SelenideElement extractQRCodeFromCell(final SelenideElement qrCodeCell) {
        return qrCodeCell.$("flow-component-renderer img");
    }

    public static SelenideElement extractButtonFromCell(final SelenideElement actionsCells) {
        return actionsCells.$("flow-component-renderer vaadin-button");
    }

    public static SelenideElement extractQRCodeFromModal() {
        return BIG_QR_CODE_MODAL.$("flow-component-renderer div img");
    }
}
