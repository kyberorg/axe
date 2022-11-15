package io.kyberorg.yalsee.senders;

import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.internal.LinkServiceInput;
import io.kyberorg.yalsee.mail.letters.AccountConfirmationLetter;
import io.kyberorg.yalsee.mail.letters.Letter;
import io.kyberorg.yalsee.mail.letters.LoginVerificationLetter;
import io.kyberorg.yalsee.mail.letters.PasswordResetLetter;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.mail.EmailSenderService;
import io.kyberorg.yalsee.services.mail.LetterType;
import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailTokenSender extends TokenSender {
    private static final String TAG = "[" + MailTokenSender.class.getSimpleName() + "]";
    private static final String ERR_TOKEN_IS_EMPTY = "Token is NULL";
    public static final String ERR_EMAIL_IS_EMPTY = "Email is empty";
    public static final String ERR_NO_LETTER_FOR_TYPE = "There is no letter template found for given Token Type";
    private final EmailSenderService emailSenderService;
    private final LinkService linkService;
    private final AppUtils appUtils;

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
        //short link
        if (shouldCreateShortLink(token.getTokenType())) {
            OperationResult shortifyResult = makeShortLink(link);
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
            mimeMessage = emailSenderService.createLetter(letterType, email, subject, vars);
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
        //send email
        emailSenderService.sendEmail(email, mimeMessage);
        return OperationResult.success();
    }

    private OperationResult makeShortLink(final String longLink) {
        String ident;
        do {
            ident = IdentGenerator.generateTokenIdent(TokenType.ACCOUNT_CONFIRMATION_TOKEN);
        } while (linkService.isLinkWithIdentExist(ident).ok());

        LinkServiceInput input = LinkServiceInput.builder(longLink).customIdent(ident).build();
        OperationResult createLinkResult = linkService.createLink(input);
        if (createLinkResult.ok()) {
            //create short link
            final String shortLink = appUtils.getShortUrl() + "/" + ident;
            return OperationResult.success().addPayload(shortLink);
        } else {
            return createLinkResult;
        }
    }

    private Letter getLetter(final Token token) {
        return switch (token.getTokenType()) {
            case ACCOUNT_CONFIRMATION_TOKEN -> new AccountConfirmationLetter(token);
            case LOGIN_VERIFICATION_TOKEN -> new LoginVerificationLetter(token);
            case PASSWORD_RESET_TOKEN -> new PasswordResetLetter(token);
            case USER_API_TOKEN -> null;
        };
    }
}
