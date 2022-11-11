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
@Table(name = "user_settings")
public class UserSettings extends BaseModel {
    @OneToOne(optional = false)
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
     * Creates new {@link UserSettings}.
     *
     * @param user {@link UserSettings} owner.
     * @return created {@link UserSettings}
     */
    public static UserSettings createForUser(final User user) {
        UserSettings up = new UserSettings();
        up.setUser(user);
        return up;
    }
}