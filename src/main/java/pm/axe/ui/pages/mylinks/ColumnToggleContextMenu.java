package pm.axe.ui.pages.mylinks;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;

/**
 * Context Menu for toggling Grid Columns. Shows or hides Grid columns.
 *
 * @since 3.10
 */
public class ColumnToggleContextMenu extends ContextMenu {
    /**
     * Creates {@link ColumnToggleContextMenu} and links it with given {@link Component}.
     *
     * @param target component (normally {@link Button}), which opens this Menu.
     */
    public ColumnToggleContextMenu(final Component target) {
        super(target);
        setOpenOnClick(true);
    }

    /**
     * Populates menu with all Grid's columns. This method uses {@link Grid.Column#getKey()} as labels.
     *
     * @param grid grid to get columns from.
     */
    public void addColumnsFromGrid(final Grid<?> grid) {
        for (Grid.Column<?> column : grid.getColumns()) {
            addItem(column.getKey(), column);
        }
    }

    private void addItem(final String label, final Grid.Column<?> column) {
        MenuItem menuItem = this.addItem(label, e -> column.setVisible(e.getSource().isChecked()));
        menuItem.setCheckable(true);
        menuItem.setChecked(column.isVisible());
    }
}
