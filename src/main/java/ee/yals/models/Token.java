package ee.yals.models;

import javax.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {

    private static final String TOKEN_COLUMN = "token";
    private static final String OWNER_COLUMN = "owner";
    private static final String EXPIRATION_TIME_COLUMN = "expiration_time";
    private static final String EXPIRED_COLUMN = "expired";

    @Id
    @GeneratedValue
    private long id;

    @Column(name = TOKEN_COLUMN, unique = true, nullable = false)
    private String token;

    @Column(name = OWNER_COLUMN, nullable = false)
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
}
