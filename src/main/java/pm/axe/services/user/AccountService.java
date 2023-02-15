package pm.axe.services.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import pm.axe.db.dao.AccountDao;
import pm.axe.db.models.Account;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.services.mail.MailService;
import pm.axe.users.AccountType;
import pm.axe.utils.crypto.SymmetricCryptTool;

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

    private final AccountDao accountDao;
    private final SymmetricCryptTool cryptTool;
    private final UserService userService;
    private final MailService mailService;

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
     * Creates {@link AccountType#LOCAL} account.
     *
     * @param user {@link Account}'s owner.
     * @return {@link OperationResult} with created {@link Account} in payload or {@link OperationResult} with error.
     */
    public OperationResult createLocalAccount(final User user) {
        Account localAccount = Account.create(AccountType.LOCAL).forUser(user);
        localAccount.setConfirmed(false);

        String encryptedName;
        OperationResult encryptNameResult = cryptTool.encrypt(user.getUsername());
        if (encryptNameResult.ok()) {
            encryptedName = encryptNameResult.getStringPayload();
        } else {
            log.error("{} Name encryption failed. Value: {}. OpResult: {}", TAG, user.getUsername(), encryptNameResult);
            return OperationResult.generalFail().withMessage(ERR_ENCRYPTION_FAILED);
        }
        localAccount.setAccountName(encryptedName);

        try {
            accountDao.save(localAccount);
            log.info("{} Created local account for {} {}", TAG, User.class.getSimpleName(), user.getUsername());
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
        OperationResult emailValidationResult = mailService.isEmailValid(email);
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
            log.error("{} Email encryption failed. Value: {}. Error: {}", TAG, email, encryptEmailResult.getMessage());
            return OperationResult.generalFail().withMessage(ERR_ENCRYPTION_FAILED);
        }

        Account emailAccount = Account.create(AccountType.EMAIL).forUser(user);
        emailAccount.setAccountName(encryptedEmail);
        emailAccount.setConfirmed(false);

        try {
            accountDao.save(emailAccount);
            log.info("{} Created email account for {} {}", TAG, User.class.getSimpleName(), user.getUsername());
            return OperationResult.success().addPayload(emailAccount);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Creates {@link AccountType#TELEGRAM} account.
     *
     * @param user  {@link Account}'s owner.
     * @param tgUser string with telegram address.
     * @param chatId active chat id for sending messages to.
     * @return {@link OperationResult} with created {@link Account} in payload or {@link OperationResult} with error.
     */
    public OperationResult createTelegramAccount(final User user, final String tgUser, final long chatId) {
        if (isAccountAlreadyExists(tgUser, AccountType.TELEGRAM)) {
            return OperationResult.conflict().withMessage(ERR_EMAIL_ALREADY_EXISTS);
        }

        String encryptedTelegramUser;
        OperationResult encryptTgUserResult = cryptTool.encrypt(tgUser);
        if (encryptTgUserResult.ok()) {
            encryptedTelegramUser = encryptTgUserResult.getStringPayload();
        } else {
            log.error("{} Telegram encryption failed. Value: {}. Error: {}",
                    TAG, tgUser, encryptTgUserResult.getMessage());
            return OperationResult.generalFail().withMessage(ERR_ENCRYPTION_FAILED);
        }

        Account telegramAccount = Account.create(AccountType.TELEGRAM).forUser(user);
        telegramAccount.setAccountName(encryptedTelegramUser);
        telegramAccount.setConfirmed(true);
        telegramAccount.setExtraInfo(chatId + "");

        try {
            accountDao.save(telegramAccount);
            log.info("{} Created telegram account for {} {}", TAG, User.class.getSimpleName(), user.getUsername());
            return OperationResult.success().addPayload(telegramAccount);
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
            accountToConfirm.setConfirmed(true);
            accountDao.save(accountToConfirm);

            User accountOwner = accountToConfirm.getUser();
            if (accountOwner.isStillUnconfirmed()) {
                userService.confirmUser(accountOwner);
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
     * Searches {@link Account} by plain-text {@link Account} name and {@link AccountType}.
     *
     * @param plainAccountName non-encrypted string with {@link Account} name.
     * @param accountType type of {@link Account}
     * @return {@link Optional} with found {@link Account} or {@link Optional#empty()} if nothing found.
     */
    public Optional<Account> getAccountByAccountName(final String plainAccountName, final AccountType accountType) {
        if (StringUtils.isBlank(plainAccountName)) return Optional.empty();
        this.accountToSearch = plainAccountName;
        List<Account> accounts = accountDao.findByType(accountType);
        Account account = accounts.parallelStream()
                .filter(this::accountHasGivenAccountName)
                .findFirst()
                .orElse(null);
        return Optional.ofNullable(account);
    }

    /**
     * Gets all {@link Account}s with given {@link AccountType}.
     *
     * @param accountType type of {@link Account}.
     *
     * @return {@link List} with found {@link Account} records.
     */
    public List<Account> getAllAccountsByType(final AccountType accountType) {
        return accountDao.findByType(accountType);
    }

    /**
     * Decrypts account name.
     *
     * @param account {@link Account} record.
     * @return {@link Optional} with decrypted {@link String} or {@link Optional#empty()}
     */
    public Optional<String> decryptAccountName(final Account account) {
        if (account != null && StringUtils.isNotBlank(account.getAccountName())) {
            //adding exception for AppUser - it is created by liquibase directly and therefore not encrypted.
            if (account.getUser().getId() == 1) {
                return Optional.of(account.getAccountName());
            }
            //for other Accounts decryption needed
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

    /**
     * Gets all {@link Account} records linked with given {@link User}.
     *
     * @param user {@link Account} owner.
     * @return list of {@link User}'s {@link Account}s.
     */
    public List<Account> getAllAccountsLinkedWithUser(final User user) {
        return accountDao.findByUser(user);
    }

    private boolean accountHasGivenAccountName(final Account account) {
        Optional<String> decryptedAccountName = decryptAccountName(account);
        return decryptedAccountName.map(accountName -> accountName.equals(accountToSearch)).orElse(false);
    }

    /**
     * Deletes {@link Account}.
     *
     * @param account {@link Account} to delete.
     *
     * @return {@link OperationResult#success()} or {@link OperationResult} with error.
     */
    public OperationResult deleteAccount(final Account account) {
        try {
            accountDao.delete(account);
            return OperationResult.success();
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to confirm account got exception {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Updates {@link AccountType#EMAIL} {@link Account}.
     * This method marks updated {@link AccountType#EMAIL} {@link Account} as unconfirmed.
     *
     * @param user {@link Account} owner.
     *
     * @param email non-empty string with new Email Address (plain value).
     *
     * @return {@link OperationResult#success()} with updated {@link Account} as payload,
     *         {@link OperationResult#malformedInput()}, when new email address not valid.
     *         {@link OperationResult#generalFail()} with {@link #ERR_ENCRYPTION_FAILED} message,
     *          when encryption failed.
     */
    public OperationResult updateEmailAccount(final User user, final String email) {
        if (user == null) return OperationResult.malformedInput().withMessage("User cannot be NULL");
        OperationResult validationResult = mailService.isEmailValid(email);
        if (validationResult.notOk()) {
            return validationResult;
        }


        Optional<Account> emailAccount = getAccount(user, AccountType.EMAIL);
        boolean userHasEmailAccount;
        if (emailAccount.isPresent()) {
            userHasEmailAccount = true;
            String encryptedEmail;
            OperationResult encryptEmailResult = cryptTool.encrypt(email);
            if (encryptEmailResult.ok()) {
                encryptedEmail = encryptEmailResult.getStringPayload();
            } else {
                log.error("{} Email encryption failed. Value: {}. Error: {}",
                        TAG, email, encryptEmailResult.getMessage());
                return OperationResult.generalFail().withMessage(ERR_ENCRYPTION_FAILED);
            }
            emailAccount.get().setAccountName(encryptedEmail);
            emailAccount.get().setConfirmed(false);
        } else {
            userHasEmailAccount = false;
        }

        try {
            if (userHasEmailAccount) {
                accountDao.save(emailAccount.get());
                log.info("{} Updated email account for {} {}", TAG, User.class.getSimpleName(), user.getUsername());
                return OperationResult.success().addPayload(emailAccount.get());
            } else {
                return createEmailAccount(user, email);
            }
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * This method restores values from old {@link Account}.
     *
     * @param oldAccount old {@link Account} to restore values from.
     * @throws IllegalArgumentException when old account is NULL.
     */
    public void rollbackAccount(final Account oldAccount) {
        if (oldAccount == null) throw new IllegalArgumentException("old account cannot be null");
        Optional<Account> currentAccount = this.getAccount(oldAccount.getUser(), oldAccount.getType());
        try {
            if (currentAccount.isPresent()) {
                currentAccount.get().copy(oldAccount);
            } else {
                accountDao.save(oldAccount);
            }
            log.info("{} Account {} rolled back", TAG, oldAccount);
        } catch (CannotCreateTransactionException e) {
            log.error("{} failed to rollback account: Database is Down", TAG);
        } catch (Exception e) {
            log.error("{} failed  to rollback Account {}: got exception {}", TAG, oldAccount, e.getMessage());
        }
    }
}
