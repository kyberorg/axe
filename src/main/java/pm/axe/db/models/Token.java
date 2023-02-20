package pm.axe.db.models;

import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import pm.axe.users.TokenType;
import pm.axe.users.TokenValueType;
import pm.axe.utils.AppUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tokens")
public class Token extends TimeModel {
    public static final int TELEGRAM_TOKEN_LEN = 8;
    /**
     * Default length of Token (code).
     */
    public static final int CODE_TOKEN_LEN = 6;
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter(value = AccessLevel.PUBLIC)
    @OneToOne
    @JoinColumn(name = "confirmation_for")
    private Account confirmationFor;

    @Column(name = "not_valid_after", nullable = false)
    private Timestamp notValidAfter;

    /**
     * Starts creating {@link Token} of given {@link TokenType}.
     *
     * @param tokenType desired {@link TokenType}
     * @return {@link Token.Builder} to continue building {@link Token}.
     */
    public static Token.Builder create(final TokenType tokenType) {
        return new Token.Builder(tokenType);
    }

    /**
     * Checks {@link Token}'s validity.
     *
     * @return true - if token is still valid, false - if expired.
     */
    public boolean isStillValid() {
        return notValidAfter.after(TimeModel.now());
    }

    /**
     * Opposite version of {@link #isStillValid()}.
     *
     * @return true - if token expired, false - if not.
     */
    public boolean isExpired() {
        return !isStillValid();
    }

    /**
     * Updates Token Value and resets its lifetime (period when token considered valid, see {@link #isStillValid()}).
     */
    public void updateToken() {
        this.token = generateNewToken(this.tokenType.getValueType());
        updateTokenLifeTime();
    }

    private static Token createToken(final TokenType type, final User user) {
        Token newToken = new Token();
        newToken.token = generateNewToken(type.getValueType());
        newToken.tokenType = type;
        newToken.user = user;
        newToken.notValidAfter = setTokenLifeTime(newToken);
        return newToken;
    }

    private static String generateNewToken(final TokenValueType tokenValueType) {
        return switch (tokenValueType) {
            case CODE -> RandomStringUtils.randomNumeric(CODE_TOKEN_LEN);
            case UUID -> UUID.randomUUID().toString();
            case TELEGRAM_TOKEN -> RandomStringUtils.randomAlphanumeric(TELEGRAM_TOKEN_LEN).toLowerCase(Locale.ROOT);
        };
    }

    private static Timestamp setTokenLifeTime(final Token currentToken) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentToken.getCreated().getTime());
        cal.add(Calendar.SECOND, currentToken.tokenType.getTokenAge());
        return new Timestamp(cal.getTime().getTime());
    }

    private void updateTokenLifeTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(AppUtils.now().getTime());
        cal.add(Calendar.SECOND, this.tokenType.getTokenAge());
        this.setNotValidAfter(new Timestamp(cal.getTime().getTime()));
    }

    /**
     * Builder for {@link Token}.
     */
    public static class Builder {
        private final TokenType tokenType;

        Builder(final TokenType tokenType) {
            this.tokenType = tokenType;
        }

        /**
         * Adds {@link Token}s owner.
         *
         * @param user {@link User} that should own given {@link Token}.
         * @return built {@link Token} record.
         */
        public Token forUser(final User user) {
            return Token.createToken(tokenType, user);
        }
    }
}
