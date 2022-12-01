package pm.axe.mail.letters;

import pm.axe.db.models.Token;
import pm.axe.mail.LetterType;
import pm.axe.users.UsernameGenerator;

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
     * @return string with username if username, provided by user or empty string, if name is generated.
     */
    default String getUsername(final Token token) {
        String username = token.getUser().getUsername();
        return UsernameGenerator.isGenerated(username) ? "" : username;
    }
}
