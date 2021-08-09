package io.kyberorg.yalsee.test.utils.vaadin.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.vaadin.flow.component.grid.Grid;

import static com.codeborne.selenide.CollectionCondition.sizeNotEqual;
import static com.codeborne.selenide.Selenide.$;
import static io.kyberorg.yalsee.test.utils.vaadin.VaadinUtils.$vaadin;

/**
 * Methods for Vaadin's {@link Grid}.
 *
 * @since 3.2
 */
public class GridElement {
    private String cssSelector;

    /**
     * Provides element object by its CSS selector. This method doesn't start any search.
     *
     * @param cssSelector string with valid CSS JQuery-styled selector. Ex. #myId or .myClass
     * @return created {@link GridElement}
     */
    public static GridElement byCss(final String cssSelector) {
        GridElement element = new GridElement();
        element.cssSelector = cssSelector;
        return element;
    }

    /**
     * Provides Grid's Header element.
     *
     * @return Grid's Header {@link SelenideElement}.
     */
    public SelenideElement getHeader() {
        return $vaadin(cssSelector).shadowRoot("thead#header > tr");
    }

    /**
     * Provides Grid's Header as cells collection.
     *
     * @return {@link ElementsCollection} with Grid Header's cells.
     */
    public ElementsCollection getHeadersCells() {
        return getHeader().$$("th");
    }

    /**
     * Provides Data Rows as collection of row elements.
     *
     * @return {@link ElementsCollection} of Data Rows.
     */
    public ElementsCollection getDataRows() {
        return getDataItems().$$("tr");
    }

    /**
     * Provides Data Row's cells as {@link ElementsCollection}.
     *
     * @param rowNumber data row number. Starting from 1. Direction is up-to-down.
     * @return {@link ElementsCollection} of cells in row number {@code rowNumber}.
     */
    public ElementsCollection getDataRowCells(final int rowNumber) {
        if (rowNumber > 0) {
            getDataRows().shouldBe(sizeNotEqual(0));
            return getDataRows().get(rowNumber - 1).$$("td");
        } else {
            throw new IllegalArgumentException("Row Number cannot be negative");
        }
    }

    /**
     * Searching for Cell.
     *
     * @param rowNumber  row number upside down. Starting from 0 - header row. 1 - first data row.
     * @param cellNumber cell number left2right order. Starting from 0.
     * @return {@link SelenideElement} {@literal vaadin-grid-cell-content}.
     * @throws IllegalArgumentException when arguments provided are not valid: negative number.
     * @throws ElementNotFound          when cell is not found
     */
    public SelenideElement getCell(int rowNumber, int cellNumber) {
        String slotName = getCellSlotName(rowNumber, cellNumber);
        return $("vaadin-grid-cell-content[slot=" + slotName + "]");
    }

    private SelenideElement getDataItems() {
        return $vaadin(cssSelector).shadowRoot("tbody#items");
    }

    private String getCellSlotName(int rowNumber, int cellNumber) throws IllegalArgumentException, ElementNotFound {
        if (rowNumber < 0) throw new IllegalArgumentException("Got negative row number");
        if (cellNumber < 0) throw new IllegalArgumentException("Got negative cell number");

        ElementsCollection row;
        if (rowNumber == 0) {
            row = getHeadersCells();
        } else {
            row = getDataRowCells(rowNumber);
        }
        row.shouldHave(sizeNotEqual(0));
        return row.get(cellNumber).$("slot").getAttribute("name");
    }
}
