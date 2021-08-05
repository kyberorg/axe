package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.GridElement;
import io.kyberorg.yalsee.ui.MyLinksView;
import lombok.Data;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object {@link MyLinksView}
 *
 * @since 3.2
 */
public class MyLinksViewPageObject {
    private static final GridElement gridElement = GridElement.byCss("vaadin-grid#" + MyLinksView.IDs.GRID);
    public static final SelenideElement PAGE = $("#" + MyLinksView.class.getSimpleName());

    private MyLinksViewPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Banners {
        public static final SelenideElement SESSION_BANNER = $("#" + MyLinksView.IDs.SESSION_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER_TEXT = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER_TEXT);
        public static final SelenideElement NO_RECORDS_BANNER_LINK = $("#" + MyLinksView.IDs.NO_RECORDS_BANNER_LINK);
    }

    public static final SelenideElement CLEAN_BUTTON = $("#" + MyLinksView.IDs.CLEAN_BUTTON);
    public static final SelenideElement GRID = $("vaadin-grid#" + MyLinksView.IDs.GRID);

    public static class Grid {
        public static class Header {
            private static Header SELF = null;

            public static Header get() {
                if (SELF == null) {
                    SELF = new Header();
                }
                return SELF;
            }

            public SelenideElement getRow() {
                return gridElement.getHeader();
            }

            public ElementsCollection getCells() {
                return gridElement.getHeadersCells();
            }

            public SelenideElement getLinkCell() {
                return gridElement.getCell(0, 0);
            }

            public SelenideElement getDescriptionCell() {
                return gridElement.getCell(0, 1);
            }

            public SelenideElement getQrCodeCell() {
                return gridElement.getCell(0, 2);
            }

            public SelenideElement getActionCell() {
                return gridElement.getCell(0, 3);
            }
        }

        public static class GridData {
            private static GridData SELF = null;

            public static GridData get() {
                if (SELF == null) {
                    SELF = new GridData();
                }
                return SELF;
            }

            public ElementsCollection getDataRows() {
                return gridElement.getDataRows();
            }

            public Row getRow(final int rowNumber) {
                return new Row(rowNumber);
            }

            @Data
            public static class Row {
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

                public SelenideElement getQRCode() {
                    return getQRCodeCell().$("flow-component-renderer img");
                }

                public SelenideElement getActionsCell() {
                    return gridElement.getCell(this.rowNumber, 3);
                }

                public SelenideElement getDeleteButton() {
                    return getActionsCell().$("flow-component-renderer vaadin-button");
                }

                public SelenideElement getItemDetails() {
                    SelenideElement itemDetailsCell = gridElement.getCell(this.rowNumber, 4);
                    return itemDetailsCell.$("div.item-details");
                }
            }
        }

        public static class GridItem {
            @Data(staticConstructor = "of")
            public static class Details {
                private final SelenideElement itemDetails;
                
                public SelenideElement getLongLink() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_LINK_CLASS);
                }

                public SelenideElement getCreatedTimeLabel() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_CREATED_TIME_LABEL_CLASS);
                }

                public SelenideElement getCreatedTime() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_CREATED_TIME_CLASS);
                }

                public SelenideElement getUpdatedTimeLabel() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS);
                }

                public SelenideElement getUpdatedTime() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_UPDATED_TIME_CLASS);
                }
            }
        }
    }

    public static void cleanSession() {
        CLEAN_BUTTON.should(exist);
        CLEAN_BUTTON.shouldBe(enabled);
        CLEAN_BUTTON.click();
    }
}
