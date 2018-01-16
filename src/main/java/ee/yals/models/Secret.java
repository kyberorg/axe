package ee.yals.models;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "secrets")
public class Secret {

    private static final String USER_COLUMN = "user";
    private static final String PASSWORD_COLUMN = "password";

    private Secret() {
    }

    @Id
    @GeneratedValue
    private long id;

    @JoinColumn(name = USER_COLUMN, nullable = false)
    @OneToOne
    private User user;

    @Column(name = PASSWORD_COLUMN, nullable = false)
    private String password;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public static Secret.Builder create(String secretStr) {
        return new Builder(secretStr);
    }

    public void updateSecretWith(String updatedSecretString) throws IllegalArgumentException {
        if (StringUtils.isBlank(updatedSecretString)) {
            throw new IllegalArgumentException("Secret cannot be empty");
        }
        this.password = updatedSecretString;
    }

    public static class Builder {
        private User owner;
        private String password;

        private Builder(String password) {
            this.password = password;
        }

        public Builder forUser(User user) {
            this.owner = user;
            return this;
        }

        public Secret please() throws IllegalStateException {
            if (Objects.isNull(owner)) {
                throw new IllegalStateException("Owner for this secret is not set. Cannot create " + Secret.class.getSimpleName());
            }
            if (StringUtils.isBlank(password)) {
                throw new IllegalStateException("Password (aka secret) is not set or empty. Cannot create " + Secret.class.getSimpleName());
            }

            Secret secret = new Secret();
            secret.user = owner;
            secret.password = password;

            return secret;
        }
    }
}
