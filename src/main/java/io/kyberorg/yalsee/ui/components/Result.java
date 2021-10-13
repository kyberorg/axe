package io.kyberorg.yalsee.ui.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Tag("yalsee-result")
public class Result extends Composite<Span> {
    public static final String SUCCESS_TEXT = "Operation successfully completed";
    public static final String FAILURE_TEXT = "Operation failed";
    @Getter
    private Icon icon;
    @Getter
    private Span text;
    @Getter
    private boolean operationSuccessful;

    public Result(final boolean operationSuccessful) {
        this.operationSuccessful = operationSuccessful;
        updateElements();
    }

    public void setOperationSuccessful(final boolean isResultPositive) {
        this.operationSuccessful = isResultPositive;
        updateElements();
    }

    public void setSuccessText(final String successText) {
        if (operationSuccessful) {
            text.setText(successText);
        }
    }

    public void setFailureText(final String failureText) {
        if (!operationSuccessful) {
            text.setText(failureText);
        }
    }

    private void updateElements() {
        if (operationSuccessful) {
            icon = VaadinIcon.CHECK.create();
            icon.setColor("green");
            icon.addClassName("space-after-icon");
            text = new Span(SUCCESS_TEXT);
            text.setClassName("green");
        } else {
            icon = VaadinIcon.CLOSE.create();
            icon.setColor("red");
            text = new Span(FAILURE_TEXT);
            text.setClassName("red");
        }
        getContent().removeAll();
        getContent().add(icon, text);
    }

}
