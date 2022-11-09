package io.kyberorg.yalsee.models;

import io.kyberorg.yalsee.users.AccountType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "user_preferences")
public class UserPreferences extends BaseModel {
    @OneToOne(cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tfa_enabled", nullable = false)
    private boolean tfaEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "tfa_channel", nullable = false)
    private AccountType tfaChannel = AccountType.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "main_channel", nullable = false)
    private AccountType mainChannel = AccountType.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "password_reset_channel")
    private AccountType passwordResetChannel;

    /**
     * Creates new {@link UserPreferences}.
     *
     * @param user {@link UserPreferences} owner.
     * @return created {@link UserPreferences}
     */
    public static UserPreferences createForUser(final User user) {
        UserPreferences up = new UserPreferences();
        up.setUser(user);
        return up;
    }
}
