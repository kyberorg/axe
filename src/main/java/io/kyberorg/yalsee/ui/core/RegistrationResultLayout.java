package io.kyberorg.yalsee.ui.core;

import com.vaadin.flow.component.html.Div;
import io.kyberorg.yalsee.ui.components.Result;

import java.text.MessageFormat;

public class RegistrationResultLayout extends YalseeLayout {

    private final Result accountCreateResult = new Result();
    private final Div accountCreatedLine = new Div();

    private final Result twoFactorResult = new Result();
    private final Div twoFactorLine = new Div();

    private final Result confirmationLetterResult = new Result();
    private final Div confirmationLetterLine = new Div();

    public RegistrationResultLayout() {
        this.setId(IDs.RESULT_LAYOUT);
    }

    public void showAccountCreatedLine(final boolean accountCreated) {
        accountCreateResult.setOperationSuccessful(accountCreated);
        accountCreateResult.setSuccessText("Account created");
        accountCreateResult.setFailureText("Failed to create account");

        accountCreatedLine.removeAll();
        accountCreatedLine.add(accountCreateResult);
        add(accountCreatedLine);
    }

    public void showTwoFactorPrefsLine(final boolean result) {
        twoFactorResult.setOperationSuccessful(result);
        twoFactorResult.setSuccessText("Two-factor authentication preferences saved");
        twoFactorResult.setFailureText("Failed to save two-factor authentication preferences");

        twoFactorLine.removeAll();
        twoFactorLine.add(twoFactorResult);
        add(twoFactorLine);
    }

    public void showConfirmationLetterLine(final boolean result, final String email) {
        confirmationLetterResult.setOperationSuccessful(result);
        confirmationLetterResult.setSuccessText(MessageFormat.format("Confirmation letter sent to {0}", email));
        confirmationLetterResult.setFailureText("Failed to sent confirmation letter");

        confirmationLetterLine.removeAll();
        confirmationLetterLine.add(confirmationLetterResult);
        add(confirmationLetterLine);
    }

    public static class IDs {
        public static final String RESULT_LAYOUT = "resultLayout";
    }
}
