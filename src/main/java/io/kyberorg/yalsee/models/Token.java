package io.kyberorg.yalsee.models;

import io.kyberorg.yalsee.users.TokenType;
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
@Table(name = "tokens")
public class Token extends TimeModel {
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @OneToOne
    @JoinColumn(name = "confirmation_for")
    private Authorization confirmationFor;

}