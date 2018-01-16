package ee.yals.models;

import javax.persistence.*;

@Entity
@Table(name = "secrets")
public class Secret {

    private static final String USER_COLUMN = "user";
    private static final String PASSWORD_COLUMN = "password";

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
}
