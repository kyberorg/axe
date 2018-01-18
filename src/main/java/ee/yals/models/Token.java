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

    public static final String TOKEN_LIFETIME_PROPERTY_KEY = "yals.token.lifetime";
    private static final long DEFAULT_TOKEN_LIFETIME = 3600;

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

    public static Token createFor(User tokenOwner) {
        if (Objects.isNull(tokenOwner)) {
            throw new IllegalArgumentException("Token Owner cannot be NULL");
        }
        Token token = new Token();
        token.token = TokenGenerator.generateNew();
        token.owner = tokenOwner;
        token.expirationTime = System.currentTimeMillis() + token.getTokenLifetime() * 1000;
        return token;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.expirationTime;
    }

    private long getTokenLifetime() {
        return Long.parseLong(System.getProperty(TOKEN_LIFETIME_PROPERTY_KEY,
                Long.toString(DEFAULT_TOKEN_LIFETIME))); //in seconds
    }
}
