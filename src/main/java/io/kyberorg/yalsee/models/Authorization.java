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
@Table(name = "authorizations")
public class Authorization extends BaseModel {

    @ManyToOne(cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    @Column(name = "auth_username", nullable = false)
    private String authUsername;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed = false;

}