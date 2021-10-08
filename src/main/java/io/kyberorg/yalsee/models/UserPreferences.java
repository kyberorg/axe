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
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE, optional = false, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tfa_enabled", nullable = false)
    private boolean tfaEnabled = false;

    @Enumerated
    @Column(name = "tfa_provider", nullable = false)
    private AuthProvider tfaProvider = AuthProvider.EMAIL;

}