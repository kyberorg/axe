package pm.axe.db.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pm.axe.Axe;
import pm.axe.internal.ExcludeFromJson;
import pm.axe.users.UserRole;
import pm.axe.utils.crypto.PasswordUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User extends TimeModel implements UserDetails {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @ToString.Exclude
    @ExcludeFromJson
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.USER;

    @Column(name = "is_confirmed")
    private boolean confirmed = false;

    /**
     * Create User.
     *
     * @param username          string with username
     * @param encryptedPassword string with encrypted password. See {@link PasswordUtils#encryptPassword(String)}
     * @return created {@link User} record.
     */
    public static User create(final String username, final String encryptedPassword) {
        User userObject = new User();
        userObject.setUsername(username);
        userObject.setPassword(encryptedPassword);
        return userObject;
    }

    /**
     * Creates pseudo-user, that indicates that no user stored.
     *
     * @return creates {@link User} with negative {@link #id} and hardcoded username.
     */
    public static User createPseudoUser() {
        User pseudoUser = new User();
        pseudoUser.setId(Axe.Defaults.NO_USER);
        pseudoUser.setUsername("Pseudo-user");
        return pseudoUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(simpleGrantedAuthority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return confirmed;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return confirmed;
    }

    /**
     * Checks {@link User} on confirmation state.
     *
     * @return true - if {@link User} is not confirmed yet, false - if it is already confirmed.
     */
    public boolean isStillUnconfirmed() {
        return !isConfirmed();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getUsername(), user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername());
    }
}
