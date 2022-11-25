package pm.axe.db.models;

import pm.axe.users.UserRole;
import pm.axe.utils.crypto.PasswordUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

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
}