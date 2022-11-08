package io.kyberorg.yalsee.models;

import io.kyberorg.yalsee.users.TokenType;
import io.kyberorg.yalsee.users.TokenValueType;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tokens")
public class Token extends TimeModel {
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Setter(value = AccessLevel.PUBLIC)
    @OneToOne
    @JoinColumn(name = "confirmation_for")
    private Account confirmationFor;

    @Column(name = "not_valid_after", nullable = false)
    private Timestamp notValidAfter;

    public static Token.Builder create(final TokenType tokenType) {
        return new Token.Builder(tokenType);
    }

    public boolean isStillValid() {
        return notValidAfter.after(TimeModel.now());
    }

    public boolean isExpired() {
        return !isStillValid();
    }

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

    private static String generateNewToken(TokenValueType tokenValueType) {
        return switch (tokenValueType) {
            case CODE -> RandomStringUtils.randomNumeric(6);
            case UUID -> UUID.randomUUID().toString();
        };
    }

    private static Timestamp setTokenLifeTime(Token currentToken) {
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

    public static class Builder {
        private final TokenType tokenType;

        Builder(final TokenType tokenType) {
            this.tokenType = tokenType;
        }

        public Token forUser(final User user) {
            return Token.createToken(tokenType, user);
        }
    }

}