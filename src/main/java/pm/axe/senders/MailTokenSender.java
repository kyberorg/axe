package pm.axe.senders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pm.axe.db.models.Token;
import pm.axe.mail.LetterType;
import pm.axe.mail.letters.AccountConfirmationLetter;
import pm.axe.mail.letters.Letter;
import pm.axe.mail.letters.LoginVerificationLetter;
import pm.axe.mail.letters.PasswordResetLetter;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.services.mail.MailSenderService;

import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;

/**
 * Sender, that sends tokens via E-Mail.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailTokenSender extends TokenSender {
    private static final String TAG = "[" + MailTokenSender.class.getSimpleName() + "]";
    private static final String ERR_TOKEN_IS_EMPTY = "Token is NULL";
    public static final String ERR_EMAIL_IS_EMPTY = "Email is empty";
    public static final String ERR_NO_LETTER_FOR_TYPE = "There is no letter template found for given Token Type";
    private final MailSenderService mailSenderService;
    private final LinkService linkService;

    @Override
    public OperationResult send(final Token token, final String email) {
        if (Objects.isNull(token)) {
            return OperationResult.malformedInput().withMessage(ERR_TOKEN_IS_EMPTY);
        }
        if (StringUtils.isBlank(email)) {
            return OperationResult.malformedInput().withMessage(ERR_EMAIL_IS_EMPTY);
        }
        Letter letter = getLetter(token);
        if (Objects.isNull(letter)) {
            return OperationResult.elementNotFound().withMessage(ERR_NO_LETTER_FOR_TYPE);
        }
        //make letter
        LetterType letterType = letter.getLetterType();
        String subject = letter.getSubject();
        Map<String, Object> vars = letter.getTemplateVars();

        //make link
        String link = letter.getLink();
        //shortify link, if needed
        if (token.getTokenType().shouldCreateShortLink()) {
            OperationResult shortifyResult = linkService.shortifyLinkForTokens(link, token);
            if (shortifyResult.ok()) {
                link = shortifyResult.getStringPayload();
            } else {
                log.warn("{} Shortification failed - sending full link instead. OpResult: {} ",
                        TAG, shortifyResult);
            }
        }
        //Override link - if needed
        vars.put("link", link);

        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSenderService.createLetter(letterType, email, subject, vars);
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
        //send email
        mailSenderService.sendEmail(email, mimeMessage);
        return OperationResult.success();
    }

    private Letter getLetter(final Token token) {
        return switch (token.getTokenType()) {
            case ACCOUNT_CONFIRMATION_TOKEN -> new AccountConfirmationLetter(token);
            case LOGIN_VERIFICATION_TOKEN -> new LoginVerificationLetter(token);
            case PASSWORD_RESET_TOKEN -> new PasswordResetLetter(token);
            case USER_API_TOKEN, TELEGRAM_CONFIRMATION_TOKEN -> null;
        };
    }
}
