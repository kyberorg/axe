package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.GridElement;
import io.kyberorg.yalsee.ui.MyLinksView;
import lombok.Data;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link MyLinksView}.
 *
 * @since 3.2
 */
public final class MyLinksViewPageObject {
    private static final GridElement GRID_ELEMENT = GridElement.byCss("vaadin-grid#" + MyLinksView.IDs.GRID);
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

    public static final SelenideElement END_SESSION_BUTTON = $("#" + MyLinksView.IDs.END_SESSION_BUTTON);
    public static final SelenideElement GRID = $("vaadin-grid#" + MyLinksView.IDs.GRID);

    public static class Grid {
        public static class Header {
            private static Header SELF = null;

            /**
             * Header object.
             *
             * @return {@link Header} singleton.
             */
            public static Header get() {
                if (SELF == null) {
                    SELF = new Header();
                }
                return SELF;
            }

            /**
             * Header Row.
             *
             * @return {@link SelenideElement} with Header Row.
             */
            public SelenideElement getRow() {
                return GRID_ELEMENT.getHeader();
            }

            /**
             * Header Cells.
             *
             * @return {@link ElementsCollection} of Header Cells.
             */
            public ElementsCollection getCells() {
                return GRID_ELEMENT.getHeadersCells();
            }

            /**
             * Link Cell.
             *
             * @return {@link SelenideElement} with Link Cell.
             */
            public SelenideElement getLinkCell() {
                return GRID_ELEMENT.getCell(0, 0);
            }

            /**
             * Description Cell.
             *
             * @return {@link SelenideElement} with Description Cell.
             */
            public SelenideElement getDescriptionCell() {
                return GRID_ELEMENT.getCell(0, 1);
            }

            /**
             * QR Code Cell.
             *
             * @return {@link SelenideElement} with QR Code Cell.
             */
            public SelenideElement getQrCodeCell() {
                return GRID_ELEMENT.getCell(0, 2);
            }

            /**
             * Action Cell.
             *
             * @return {@link SelenideElement} with Action Cell.
             */
            public SelenideElement getActionCell() {
                return GRID_ELEMENT.getCell(0, 3);
            }
        }

        public static class GridData {
            private static GridData SELF = null;

            /**
             * Grid Data object.
             *
             * @return {@link GridData} singleton.
             */
            public static GridData get() {
                if (SELF == null) {
                    SELF = new GridData();
                }
                return SELF;
            }

            /**
             * Data Rows.
             *
             * @return {@link ElementsCollection} of Data Rows.
             */
            public ElementsCollection getDataRows() {
                return GRID_ELEMENT.getDataRows();
            }

            /**
             * Data Row by its number.
             *
             * @param rowNumber row number starting from 1. Direction is up-to-down.
             * @return {@link Row}, which represents given row.
             */
            public Row getRow(final int rowNumber) {
                return new Row(rowNumber);
            }

            @Data
            public static final class Row {
                private final int rowNumber;

                private Row(final int rowNumber) {
                    if (rowNumber > 0) {
                        this.rowNumber = rowNumber;
                    } else {
                        throw new IllegalArgumentException("Row Number must be positive number");
                    }
                }

                /**
                 * Link Cell.
                 *
                 * @return {@link SelenideElement} with Link Cell.
                 */
                public SelenideElement getLinkCell() {
                    return GRID_ELEMENT.getCell(this.rowNumber, 0);
                }

                /**
                 * Description Cell.
                 *
                 * @return {@link SelenideElement} with Description Cell.
                 */
                public SelenideElement getDescriptionCell() {
                    return GRID_ELEMENT.getCell(this.rowNumber, 1);
                }

                /**
                 * Description Editor.
                 *
                 * @return {@link SelenideElement} with Description Editor.
                 */
                public SelenideElement getDescriptionEditor() {
                    return getDescriptionCell().$("flow-component-renderer vaadin-text-field");
                }

                /**
                 * QR Code Cell.
                 *
                 * @return {@link SelenideElement} with QR Code Cell.
                 */
                public SelenideElement getQRCodeCell() {
                    return GRID_ELEMENT.getCell(this.rowNumber, 2);
                }

                /**
                 * QR Code Element.
                 *
                 * @return {@link SelenideElement} with QR Code element.
                 */
                public SelenideElement getQRCode() {
                    return getQRCodeCell().$("flow-component-renderer img");
                }

                /**
                 * Actions Cell.
                 *
                 * @return {@link SelenideElement} with Actions Cell.
                 */
                public SelenideElement getActionsCell() {
                    return GRID_ELEMENT.getCell(this.rowNumber, 3);
                }

                /**
                 * Delete Button Element.
                 *
                 * @return {@link SelenideElement} with Delete Button Element.
                 */
                public SelenideElement getDeleteButton() {
                    return getActionsCell().$("flow-component-renderer vaadin-button");
                }

                /**
                 * Item Details Element.
                 *
                 * @return {@link SelenideElement} with Item Details Element.
                 */
                public SelenideElement getItemDetails() {
                    SelenideElement itemDetailsCell = GRID_ELEMENT.getCell(this.rowNumber, 4);
                    return itemDetailsCell.$("div.item-details");
                }
            }
        }

        public static class GridItem {
            @Data(staticConstructor = "of")
            public static class Details {
                private final SelenideElement itemDetails;

                /**
                 * Long Link Element.
                 *
                 * @return {@link SelenideElement} with Long Link Element.
                 */
                public SelenideElement getLongLink() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_LINK_CLASS);
                }

                /**
                 * Label for Created Time.
                 *
                 * @return {@link SelenideElement} with Created Time Label.
                 */
                public SelenideElement getCreatedTimeLabel() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_CREATED_TIME_LABEL_CLASS);
                }

                /**
                 * Created Time Span.
                 *
                 * @return {@link SelenideElement} with Created Time Span.
                 */
                public SelenideElement getCreatedTime() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_CREATED_TIME_CLASS);
                }

                /**
                 * Label for Updated Time.
                 *
                 * @return {@link SelenideElement} with Updated Time Label.
                 */
                public SelenideElement getUpdatedTimeLabel() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS);
                }

                /**
                 * Updated Time Span.
                 *
                 * @return {@link SelenideElement} with Updated Time Span.
                 */
                public SelenideElement getUpdatedTime() {
                    return itemDetails.$("." + MyLinksView.IDs.ITEM_DETAILS_UPDATED_TIME_CLASS);
                }
            }

            public static class BigQRCodeModal {
                public static final SelenideElement MODAL = $("vaadin-dialog-overlay#overlay");
                public static final SelenideElement QR_CODE = MODAL.$("flow-component-renderer div img");
            }
        }
    }

    /**
     * Cleans current session by clicking {@link #END_SESSION_BUTTON}.
     */
    public static void cleanSession() {
        END_SESSION_BUTTON.click();
    }
}
