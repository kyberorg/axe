package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.dao.AccountDao;
import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.mail.EmailService;
import io.kyberorg.yalsee.users.AccountType;
import io.kyberorg.yalsee.utils.crypto.SymmetricCryptTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service that handles Account issues.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private static final String TAG = "[" + AccountService.class.getSimpleName() + "]";
    private static final String ERR_ACCOUNT_IS_EMPTY = "Account is null";
    private static final String ERR_USER_HAS_NO_LOCAL_ACCOUNT = "User has no local account";

    private final AccountDao accountDao;
    private final SymmetricCryptTool cryptTool;
    private final UserService userService;
    private final EmailService emailService;

    private static final String ERR_EMAIL_ALREADY_EXISTS = "Email already used";
    private static final String ERR_ENCRYPTION_FAILED = "Failed to encrypt plain text value before saving";

    private String accountToSearch;

    /**
     * Checks if {@link Account} exists.
     *
     * @param accountName string with {@link Account#accountName}.
     * @param accountType {@link Account#type}
     * @return true - if account with given name exists, false - is no such record.
     */
    public boolean isAccountAlreadyExists(final String accountName, final AccountType accountType) {
        if (StringUtils.isBlank(accountName)) return false;
        this.accountToSearch = accountName;

        List<Account> accounts = accountDao.findByType(accountType);
        Account sameAccount = accounts.parallelStream()
                .filter(this::accountHasGivenAccountName)
                .findFirst()
                .orElse(null);
        return Objects.nonNull(sameAccount);
    }

    /**
     * Opposite of {@link #isAccountAlreadyExists(String, AccountType)}.
     *
     * @param accountName string with {@link Account#accountName}.
     * @param accountType {@link Account#type}
     * @return true - if account with given name not exist, false - if exists.
     */
    public boolean isAccountUnique(final String accountName, final AccountType accountType) {
        return !isAccountAlreadyExists(accountName, accountType);
    }

    /**
     * Creates {@link AccountType#LOCAL} account.
     *
     * @param user {@link Account}'s owner.
     * @return {@link OperationResult} with created {@link Account} in payload or {@link OperationResult} with error.
     */
    public OperationResult createLocalAccount(final User user) {
        Account localAccount = Account.create(AccountType.LOCAL).forUser(user);
        localAccount.setAccountName(user.getUsername());
        localAccount.setConfirmed(false);

        log.debug("{} local account to create {}", TAG, localAccount);

        try {
            accountDao.save(localAccount);
            log.info("{} created local account for {} {}", TAG, User.class.getSimpleName(), user.getUsername());
            return OperationResult.success().addPayload(localAccount);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Creates {@link AccountType#EMAIL} account.
     *
     * @param user  {@link Account}'s owner.
     * @param email string with email address.
     * @return {@link OperationResult} with created {@link Account} in payload or {@link OperationResult} with error.
     */
    public OperationResult createEmailAccount(final User user, final String email) {
        OperationResult emailValidationResult = emailService.isEmailValid(email);
        if (emailValidationResult.notOk()) {
            return emailValidationResult;
        }

        if (isAccountAlreadyExists(email, AccountType.EMAIL)) {
            return OperationResult.conflict().withMessage(ERR_EMAIL_ALREADY_EXISTS);
        }

        String encryptedEmail;
        OperationResult encryptEmailResult = cryptTool.encrypt(email);
        if (encryptEmailResult.ok()) {
            encryptedEmail = encryptEmailResult.getStringPayload();
        } else {
            log.error("{} email encryption failed. Value: {}. Error: {}", TAG, email, encryptEmailResult.getMessage());
            return OperationResult.generalFail().withMessage(ERR_ENCRYPTION_FAILED);
        }

        Account emailAccount = Account.create(AccountType.EMAIL).forUser(user);
        emailAccount.setAccountName(encryptedEmail);
        emailAccount.setConfirmed(false);

        try {
            accountDao.save(emailAccount);
            log.info("{} created email account for {} {}", TAG, User.class.getSimpleName(), user.getUsername());
            return OperationResult.success().addPayload(emailAccount);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Sets that {@link Account} confirmed.
     *
     * @param accountToConfirm account to confirm.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error.
     */
    public OperationResult confirmAccount(final Account accountToConfirm) {
        try {
            if (accountToConfirm == null) {
                return OperationResult.generalFail().withMessage(ERR_ACCOUNT_IS_EMPTY);
            }

            // confirming account
            accountToConfirm.setConfirmed(true);
            accountDao.save(accountToConfirm);

            User user = accountToConfirm.getUser();
            Optional<Account> userLocalAccount =
                    accountDao.findByUserAndType(user, AccountType.LOCAL);
            if (userLocalAccount.isEmpty()) {
                log.error("{} User {} has no {} account. System Bug.",
                        TAG, user.getUsername(), AccountType.LOCAL.name());
                return OperationResult.generalFail().withMessage(ERR_USER_HAS_NO_LOCAL_ACCOUNT);
            }

            //if first non-local account confirmed  - confirm local account as well
            boolean localAccountConfirmed = userLocalAccount.get().isConfirmed();
            if (!localAccountConfirmed) {
                userLocalAccount.get().setConfirmed(true);
                accountDao.save(userLocalAccount.get());
            }
            if (!user.isEnabled()) {
                userService.enableUser(user);
            }
            return OperationResult.success();
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to confirm account got exception {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Get Account by {@link User} and {@link AccountType}.
     *
     * @param user        account's owner
     * @param accountType account's type
     * @return {@link Optional} with found {@link Account} or {@link Optional#empty()}.
     */
    public Optional<Account> getAccount(final User user, final AccountType accountType) {
        return accountDao.findByUserAndType(user, accountType);
    }

    /**
     * Decrypts account name.
     *
     * @param account {@link Account} record.
     * @return {@link Optional} with decrypted {@link String} or {@link Optional#empty()}
     */
    public Optional<String> decryptAccountName(final Account account) {
        if (account != null && StringUtils.isNotBlank(account.getAccountName())) {
            OperationResult result = cryptTool.decrypt(account.getAccountName());
            if (result.ok()) {
                return Optional.of(result.getStringPayload());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private boolean accountHasGivenAccountName(final Account account) {
        OperationResult result = cryptTool.decrypt(account.getAccountName());
        if (result.ok()) {
            String valueFromDb = result.getStringPayload();
            if (StringUtils.isNotBlank(valueFromDb)) {
                return valueFromDb.equals(accountToSearch);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
