package ee.yals.models;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * Links table mapped to Java Object
 *
 * @since 3.0
 */
@Entity
@Table(name = "users")
public class User {

    private static final String ALIAS_COLUMN = "alias";
    private static final String CREATED_COLUMN = "created";
    private static final String UPDATED_COLUMN = "updated";

    private User() {
    }

    @Id
    @GeneratedValue
    private long id;

    @Column(name = ALIAS_COLUMN, nullable = false, unique = true)
    private String alias;

    @Column(name = CREATED_COLUMN, nullable = false)
    private long created;

    @Column(name = UPDATED_COLUMN, nullable = false)
    private long updated;

    public long getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public static User create(String userName) throws IllegalArgumentException {
        if (StringUtils.isBlank(userName)) {
            throw new IllegalArgumentException("Username (aka Alias) cannot be empty");
        }

        long now = System.currentTimeMillis();

        User u = new User();
        u.alias = userName;
        u.created = now;
        u.updated = now;

        return u;
    }

    public User updateAliasWith(String newAliasName) throws IllegalArgumentException {
        if (StringUtils.isBlank(newAliasName)) {
            throw new IllegalArgumentException("Username (aka Alias) cannot be empty");
        }

        this.alias = newAliasName;
        return this;
    }

    public User updateUpdated() {
        this.updated = System.currentTimeMillis();
        return this;
    }
}
