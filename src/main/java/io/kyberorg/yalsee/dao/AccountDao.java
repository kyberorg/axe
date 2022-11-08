package io.kyberorg.yalsee.dao;

import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.users.AccountType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountDao extends CrudRepository<Account, Long> {
    Optional<Account> findByUserAndAccountType(User user, AccountType accountType);

    List<Account> findByAccountType(AccountType provider);
}