package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.GridElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.TextFieldElement;
import io.kyberorg.yalsee.ui.pages.mylinks.MyLinksPage;
import lombok.Data;

import static com.codeborne.selenide.Selenide.*;
import static io.kyberorg.yalsee.constants.App.FOUR;
import static io.kyberorg.yalsee.constants.App.THREE;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Page Object for {@link MyLinksPage}.
 *
 * @since 3.2
 */
public final class MyLinksViewPageObject {
    private static final GridElement GRID_ELEMENT = GridElement.byCss("vaadin-grid#" + MyLinksPage.IDs.GRID);
    public static final SelenideElement PAGE = $("#" + MyLinksPage.class.getSimpleName());

    private MyLinksViewPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Banners {
        public static final SelenideElement SESSION_BANNER = $("#" + MyLinksPage.IDs.SESSION_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER = $("#" + MyLinksPage.IDs.NO_RECORDS_BANNER);
        public static final SelenideElement NO_RECORDS_BANNER_TEXT = $("#" + MyLinksPage.IDs.NO_RECORDS_BANNER_TEXT);
        public static final SelenideElement NO_RECORDS_BANNER_LINK = $("#" + MyLinksPage.IDs.NO_RECORDS_BANNER_LINK);
    }

    public static final SelenideElement END_SESSION_BUTTON = $("#" + MyLinksPage.IDs.END_SESSION_BUTTON);
    public static final SelenideElement TOGGLE_COLUMNS_BUTTON = $("#toggleColumnsButton");

    public static class ToggleColumnsMenu {
        public static final SelenideElement MENU_BOX = $("vaadin-context-menu-list-box");
        public static final ElementsCollection MENU_ITEMS = MENU_BOX.$$(".vaadin-menu-item");
        public static final SelenideElement LINK_ITEM = $x("//vaadin-context-menu-item[text()='Link']");
        public static final SelenideElement DESCRIPTION_ITEM =
                $x("//vaadin-context-menu-item[text()='Description']");
        public static final SelenideElement QR_CODE_ITEM =
                $x("//vaadin-context-menu-item[text()='QR Code']");
        public static final SelenideElement ACTIONS_ITEM =
                $x("//vaadin-context-menu-item[text()='Actions']");

        /**
         * Closes {@link ToggleColumnsMenu}.
         */
        public static void closeMenu() {
            $("body").pressEscape();
        }
    }

    public static final SelenideElement GRID_FILTER_FIELD = $("#gridFilterField");

    public static class GridFilter {
        public static final SelenideElement SEARCH_ICON = $("iron-icon[slot='prefix']");
        public static final SelenideElement CLEAR_BUTTON = TextFieldElement.byCss("#gridFilterField").getClearButton();

        /**
         * Sets given value to Filter's Field.
         *
         * @param value string with filter's value.
         */
        public static void setFilter(final String value) {
            GRID_FILTER_FIELD.setValue(value);
        }

        /**
         * Clean current filter's value.
         */
        public static void cleanFilter() {
            CLEAR_BUTTON.click();
        }
    }

    public static final SelenideElement GRID = $("vaadin-grid#" + MyLinksPage.IDs.GRID);

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
                return GRID_ELEMENT.getCell(0, THREE);
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
                 * Span where link text is located.
                 *
                 * @return {@link SelenideElement} with Span, where link text is.
                 */
                public SelenideElement getLinkSpan() {
                    return getLinkCell().$("span");
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
                    return GRID_ELEMENT.getCell(this.rowNumber, THREE);
                }

                /**
                 * Edit Button Element.
                 *
                 * @return {@link SelenideElement} with Edit Button Element.
                 */
                public SelenideElement getEditButton() {
                    return getActionsCell().
                            $("flow-component-renderer vaadin-horizontal-layout vaadin-button.edit-btn");
                }

                /**
                 * Delete Button Element.
                 *
                 * @return {@link SelenideElement} with Delete Button Element.
                 */
                public SelenideElement getDeleteButton() {
                    return getActionsCell().
                            $("flow-component-renderer vaadin-horizontal-layout vaadin-button.delete-btn");
                }

                /**
                 * Save Button Element.
                 *
                 * @return {@link SelenideElement} with Save Button Element.
                 */
                public SelenideElement getSaveButton() {
                    return getActionsCell().
                            $("flow-component-renderer vaadin-horizontal-layout vaadin-button.save-btn");
                }

                /**
                 * Cancel Button Element.
                 *
                 * @return {@link SelenideElement} with Cancel Button Element.
                 */
                public SelenideElement getCancelButton() {
                    return getActionsCell().
                            $("flow-component-renderer vaadin-horizontal-layout vaadin-button.cancel-btn");
                }

                /**
                 * Item Details Element.
                 *
                 * @return {@link SelenideElement} with Item Details Element.
                 */
                public SelenideElement getItemDetails() {
                    SelenideElement itemDetailsCell = GRID_ELEMENT.getCell(this.rowNumber, FOUR);
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
                    return itemDetails.$("." + MyLinksPage.IDs.ITEM_DETAILS_LINK_CLASS);
                }

                /**
                 * Label for Created Time.
                 *
                 * @return {@link SelenideElement} with Created Time Label.
                 */
                public SelenideElement getCreatedTimeLabel() {
                    return itemDetails.$("." + MyLinksPage.IDs.ITEM_DETAILS_CREATED_TIME_LABEL_CLASS);
                }

                /**
                 * Created Time Span.
                 *
                 * @return {@link SelenideElement} with Created Time Span.
                 */
                public SelenideElement getCreatedTime() {
                    return itemDetails.$("." + MyLinksPage.IDs.ITEM_DETAILS_CREATED_TIME_CLASS);
                }

                /**
                 * Label for Updated Time.
                 *
                 * @return {@link SelenideElement} with Updated Time Label.
                 */
                public SelenideElement getUpdatedTimeLabel() {
                    return itemDetails.$("." + MyLinksPage.IDs.ITEM_DETAILS_UPDATED_TIME_LABEL_CLASS);
                }

                /**
                 * Updated Time Span.
                 *
                 * @return {@link SelenideElement} with Updated Time Span.
                 */
                public SelenideElement getUpdatedTime() {
                    return itemDetails.$("." + MyLinksPage.IDs.ITEM_DETAILS_UPDATED_TIME_CLASS);
                }
            }

            public static class BigQRCodeModal {
                public static final SelenideElement MODAL = $("vaadin-dialog-overlay#overlay");
                public static final SelenideElement QR_CODE = MODAL.$("flow-component-renderer div img");
            }
        }
    }

    public static class DeleteDialog {
        public static final SelenideElement DIALOG = $(".delete-dialog");
        public static final SelenideElement TITLE = $(".delete-dialog-title");
        public static final SelenideElement MESSAGE = $(".delete-dialog-message");
        public static final SelenideElement CANCEL_BUTTON = $(".delete-dialog-cancel-btn");
        public static final SelenideElement DELETE_BUTTON = $(".delete-dialog-delete-btn");
    }

    /**
     * Cleans current session by clicking {@link #END_SESSION_BUTTON}.
     */
    public static void cleanSession() {
        END_SESSION_BUTTON.click();
    }

    /**
     * Opens MyLinks Page.
     */
    public static void openMyLinksPage() {
        open("/myLinks");
        waitForVaadin();
    }

    /**
     * Clicks first delete button.
     */
    public static void clickFirstDeleteButton() {
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.click();
    }

    /**
     * Closes Grid Editor on given Row.
     *
     * @param rowNumber row number from 1.
     */
    public static void closeGridEditorIfOpened(final int rowNumber) {
        SelenideElement descriptionEditor = Grid.GridData.get().getRow(rowNumber).getDescriptionEditor();
        if (descriptionEditor.exists()) {
            descriptionEditor.pressEnter();
        }
    }
}
