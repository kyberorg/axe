package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class RegistrationResultLayout extends YalseeLayout {

    private final HorizontalLayout accountCreatedLine = new HorizontalLayout();
    private final Span accountCreatedText = new Span();

    private final HorizontalLayout twoFactorPrefsLine = new HorizontalLayout();
    private final Span twoFactorPrefsText = new Span();

    private final HorizontalLayout confirmationLetterLine = new HorizontalLayout();
    private final Span confirmationLetterText = new Span();

    public RegistrationResultLayout() {
        init();
    }

    private void init() {
        this.setId(IDs.RESULT_LAYOUT);
        add(accountCreatedLine, twoFactorPrefsLine, confirmationLetterLine);
    }

    public void showAccountCreatedLine(final boolean accountCreated) {
        Icon accountCreatedIcon;
        if (accountCreated) {
            accountCreatedIcon = VaadinIcon.CHECK.create();
            accountCreatedIcon.setColor("green");
            accountCreatedText.setText("Account created");
            accountCreatedText.setClassName("green");
        } else {
            accountCreatedIcon = VaadinIcon.CLOSE.create();
            accountCreatedIcon.setColor("red");
            accountCreatedText.setText("Failed to create account");
            accountCreatedText.setClassName("red");
        }
        accountCreatedLine.removeAll();
        accountCreatedLine.add(accountCreatedIcon, accountCreatedText);
    }

    public void showTwoFactorPrefsLine(final boolean result) {
        Icon icon;
        if (result) {
            icon = VaadinIcon.CHECK.create();
            icon.setColor("green");
            twoFactorPrefsText.setText("Two-factor authentication preferences saved");
            twoFactorPrefsText.setClassName("green");
        } else {
            icon = VaadinIcon.CLOSE.create();
            icon.setColor("red");
            twoFactorPrefsText.setText("Failed to save 2fa preferences");
            twoFactorPrefsText.setClassName("red");
        }
        twoFactorPrefsLine.removeAll();
        twoFactorPrefsLine.add(icon, twoFactorPrefsText);
    }

    public void showConfirmationLetterLine(final boolean result, final String email) {
        Icon icon;
        if (result) {
            icon = VaadinIcon.CHECK.create();
            icon.setColor("green");
            confirmationLetterText.setText("Confirmation letter sent to " + email);
            confirmationLetterText.setClassName("green");
        } else {
            icon = VaadinIcon.CLOSE.create();
            icon.setColor("red");
            confirmationLetterText.setText("Failed to save 2fa preferences");
            confirmationLetterText.setClassName("red");
        }
        confirmationLetterLine.removeAll();
        confirmationLetterLine.add(icon, confirmationLetterText);
    }

    public static class IDs {
        public static final String RESULT_LAYOUT = "resultLayout";
    }
}
