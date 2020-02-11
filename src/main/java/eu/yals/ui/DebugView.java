package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import org.vaadin.olli.ClipboardHelper;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = AppView.class)
@Caption("Debug Page")
@Icon(VaadinIcon.FLASK)
@PageTitle("Link shortener for friends: Debug Page")
public class DebugView extends Div {
  public DebugView() {
    setId(DebugView.class.getSimpleName());

    Button button = new Button("click this button to copy some stuff to the clipboard");
    Span span = new Span("");
    button.addClickListener(event -> span.setText("Button clicked"));
    ClipboardHelper clipboardHelper = new ClipboardHelper("some stuff", button);
    TextField input = new TextField("Insert some stuff here");
    input.setValueChangeMode(ValueChangeMode.EAGER);
    input.addValueChangeListener(event -> Notification.show(event.getValue()).setPosition(Notification.Position.MIDDLE));

    add(clipboardHelper, input, span);
  }
}
