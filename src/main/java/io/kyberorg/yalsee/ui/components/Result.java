package io.kyberorg.yalsee.ui.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@Tag("yalsee-result")
public class Result extends Composite<Span> {
    public static final String DEFAULT_SUCCESS_TEXT = "Operation successfully completed";
    public static final String DEFAULT_FAILURE_TEXT = "Operation failed";
    @Getter
    private Icon icon;
    @Getter
    private Span textSpan;
    @Getter
    private boolean operationSuccessful;

    private String successText;
    private String failureText;

    public Result(final boolean operationSuccessful) {
        this.operationSuccessful = operationSuccessful;
        updateElements();
    }

    public void setOperationSuccessful(final boolean isResultPositive) {
        this.operationSuccessful = isResultPositive;
        updateElements();
    }

    public void setSuccessText(final String successText) {
        this.successText = successText;
        updateElements();
    }

    public void setFailureText(final String failureText) {
        this.failureText = failureText;
        updateElements();
    }

    private void updateElements() {
        if (operationSuccessful) {
            icon = VaadinIcon.CHECK.create();
            icon.setColor("green");
            icon.addClassName("space-after-icon");
            textSpan = new Span();
            textSpan.setClassName("green");
            String text = StringUtils.isNotBlank(this.successText) ? successText : DEFAULT_SUCCESS_TEXT;
            textSpan.setText(text);
        } else {
            icon = VaadinIcon.CLOSE.create();
            icon.setColor("red");
            textSpan = new Span();
            textSpan.setClassName("red");
            String text = StringUtils.isNotBlank(this.failureText) ? failureText : DEFAULT_FAILURE_TEXT;
            textSpan.setText(text);
        }
        getContent().removeAll();
        getContent().add(icon, textSpan);
    }
}
