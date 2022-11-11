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
@Table(name = "accounts")
public final class Account extends BaseModel {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed = false;

    /**
     * Starts to create new {@link Account} of given {@link AccountType}.
     *
     * @param accountType desired {@link AccountType}.
     * @return {@link Account.Builder} to complete building {@link Account}.
     */
    public static Account.Builder create(final AccountType accountType) {
        return new Builder(accountType);
    }

    private Account(final AccountType accountType, final User user) {
        this.type = accountType;
        this.user = user;
    }

    public static class Builder {
        private final AccountType accountType;

        Builder(final AccountType accountType) {
            this.accountType = accountType;
        }

        /**
         * Sets owner of {@link Account}.
         *
         * @param user accounts owner.
         * @return built {@link Account} record.
         */
        public Account forUser(final User user) {
            return new Account(accountType, user);
        }
    }
}
