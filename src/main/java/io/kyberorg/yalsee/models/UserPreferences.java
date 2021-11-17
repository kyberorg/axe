package io.kyberorg.yalsee.models;

import io.kyberorg.yalsee.users.AuthProvider;
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
    private AuthProvider tfaChannel = AuthProvider.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "main_channel", nullable = false)
    private AuthProvider mainChannel = AuthProvider.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "password_reset_channel")
    private AuthProvider passwordResetChannel;
}