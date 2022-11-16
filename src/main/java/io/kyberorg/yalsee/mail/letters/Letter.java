package io.kyberorg.yalsee.mail.letters;

import io.kyberorg.yalsee.mail.LetterType;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.users.UsernameGenerator;

import java.util.Map;

/**
 * Interface with methods needed to create mail letters.
 */
public interface Letter {
    /**
     * Get corresponding {@link LetterType}.
     *
     * @return corresponding {@link LetterType}
     */
    LetterType getLetterType();

    /**
     * Letter subject.
     *
     * @return string with Mail Subject
     */
    String getSubject();

    /**
     * Constructs link, that should be added to letter.
     *
     * @return string with constructed link.
     */
    String getLink();

    /**
     * Provides Template Variables.
     *
     * @return {@link Map} with template variables.
     */
    Map<String, Object> getTemplateVars();

    /**
     * Extracts Username from Token.
     *
     * @param token non-empty {@link Token} to extract Username from.
     * @return string with username if username, provided by user or pre-defined string, if name is generated.
     */
    default String getUsername(final Token token) {
        String username = token.getUser().getUsername();
        return UsernameGenerator.isGenerated(username) ? "Yalsee User" : username;
    }
}
