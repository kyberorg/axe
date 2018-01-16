package ee.yals.models;

import ee.yals.core.TokenGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tokens")
public class Token {

    private static final String TOKEN_COLUMN = "token";
    private static final String OWNER_COLUMN = "owner";
    private static final String EXPIRATION_TIME_COLUMN = "expiration_time";
    private static final String EXPIRED_COLUMN = "expired";

    public static final String TOKEN_LIFETIME_PROPERTY_KEY = "yals.token.lifetime";
    private static final long DEFAULT_TOKEN_LIFETIME = 3600;
    private static final long TOKEN_LIFETIME = Long.parseLong(System.getProperty(TOKEN_LIFETIME_PROPERTY_KEY,
            Long.toString(DEFAULT_TOKEN_LIFETIME))); //in seconds

    private Token() {
    }

    @Id
    @GeneratedValue
    private long id;

    @Column(name = TOKEN_COLUMN, unique = true, nullable = false)
    private String token;

    @JoinColumn(name = OWNER_COLUMN, nullable = false)
    @ManyToOne
    private User owner;

    @Column(name = EXPIRATION_TIME_COLUMN, nullable = false)
    private long expirationTime;

    @Column(name = EXPIRED_COLUMN, nullable = false)
    private boolean expired;

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getOwner() {
        return owner;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public static Token createFor(User tokenOwner) {
        if (Objects.isNull(tokenOwner)) {
            throw new IllegalArgumentException("Token Owner cannot be NULL");
        }
        Token token = new Token();
        token.token = TokenGenerator.generateNew();
        token.expirationTime = System.currentTimeMillis() + TOKEN_LIFETIME * 1000;
        token.expired = isExpired(token);
        return token;
    }

    public static boolean isExpired(Token tokenToCheck) {
        return System.currentTimeMillis() > tokenToCheck.expirationTime;
        //TODO this should update (notify) to DB somehow
    }
}
