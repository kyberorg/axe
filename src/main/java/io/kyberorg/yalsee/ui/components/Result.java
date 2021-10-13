package io.kyberorg.yalsee.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Tag("yalsee-result")
public class Result extends Composite<Span> implements HasComponents {
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
    private Collection<Component> customTextComponents = null;

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
        this.customTextComponents = null;
        updateElements();
    }

    public void setFailureText(final String failureText) {
        this.failureText = failureText;
        this.customTextComponents = null;
        updateElements();
    }

    public void setTextComponents(final Component... textComponents) {
        this.customTextComponents = List.of(textComponents);
        updateElements();
    }

    public void hideIcon() {
        icon.setVisible(false);
    }

    @Override
    public void removeAll() {
        HasComponents.super.removeAll();
        this.customTextComponents = null;
    }

    private void updateElements() {
        if (operationSuccessful) {
            icon = VaadinIcon.CHECK.create();
            icon.setColor("green");
            icon.addClassName("space-after-icon");
            textSpan = new Span();
            textSpan.setClassName("green");
            if (customTextComponents == null || customTextComponents.isEmpty()) {
                String text = StringUtils.isNotBlank(this.successText) ? successText : DEFAULT_SUCCESS_TEXT;
                textSpan.setText(text);
            } else {
                customTextComponents.forEach(textSpan::add);
            }
        } else {
            icon = VaadinIcon.CLOSE.create();
            icon.setColor("red");
            textSpan = new Span();
            textSpan.setClassName("red");
            if (customTextComponents == null || customTextComponents.isEmpty()) {
                String text = StringUtils.isNotBlank(this.failureText) ? failureText : DEFAULT_FAILURE_TEXT;
                textSpan.setText(text);
            } else {
                customTextComponents.forEach(textSpan::add);
            }
        }
        getContent().removeAll();
        getContent().add(icon, textSpan);
    }
}
