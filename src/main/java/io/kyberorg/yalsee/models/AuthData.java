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
@Table(name = "auth_data")
public class AuthData {
    private static final String USER_COLUMN = "user";
    private static final String PROVIDER_COLUMN = "provider";
    private static final String AUTH_USERNAME_COLUMN = "auth_username";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = USER_COLUMN, nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = PROVIDER_COLUMN, nullable = false)
    private AuthProvider authProvider;

    @Column(name = "auth_username", nullable = false)
    private String authUsername;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed = false;

}