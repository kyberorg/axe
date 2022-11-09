package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.AccountType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * DAO for {@link Account} table.
 */
public interface AccountDao extends CrudRepository<Account, Long> {
    /**
     * Searching for User's Account by its {@link AccountType}.
     *
     * @param user        account's owner
     * @param accountType type of account
     * @return {@link Optional} with {@link Account} - if found or {@link Optional#empty()} - if not.
     */
    Optional<Account> findByUserAndType(User user, AccountType accountType);

    /**
     * Lists all {@link Account}s of given {@link AccountType}.
     *
     * @param accountType type of account wanted.
     * @return list of {@link Account}s found. List can be empty, when nothing found.
     */
    List<Account> findByType(AccountType accountType);
}
