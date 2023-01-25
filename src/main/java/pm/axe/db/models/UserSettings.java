package pm.axe.db.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.threeten.extra.PeriodDuration;
import pm.axe.db.converters.PeriodDurationConverter;
import pm.axe.users.AccountType;
import pm.axe.users.LandingPage;

import javax.persistence.*;
import java.time.Period;

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
    private AccountType passwordResetChannel = AccountType.LOCAL;

    @Column(name = "dark_mode")
    private boolean darkMode = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "landing_page")
    private LandingPage landingPage;

    @Convert(converter = PeriodDurationConverter.class)
    @Column(name = "login_session_duration")
    private PeriodDuration loginSessionDuration = PeriodDuration.of(Period.ofMonths(1));


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
