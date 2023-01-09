package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;

public class TheButton extends Composite<Button> implements HasStyle {
    public TheButton() {
        getContent().setText("Push the Button");
        getContent().getElement().setAttribute("onclick", "copyToClipboard('my text')");
    }
}
