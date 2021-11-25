package io.kyberorg.yalsee.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Login table mapped Java Object. Persisting logins to implement "Persistent Login Cookie" pattern.
 *
 * @since 4.0
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "logins")
public class Login extends TimeModel {

    @Column(name = "sarja", nullable = false, unique = true)
    private String sarja;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ua", nullable = false)
    private String userAgent;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "not_valid_after", nullable = false)
    private Timestamp notValidAfter;

    public boolean isValid() {
        return notValidAfter.after(TimeModel.now());
    }
}
