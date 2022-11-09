package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.dao.AccountDao;
import io.kyberorg.yalsee.models.Account;
import io.kyberorg.yalsee.models.Token;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    public static final String TAG = "[" + AccountService.class.getSimpleName() + "]";
    private static final String ERR_NO_SUCH_TOKEN_FOUND = "No such token found";
    private static final String ERR_TOKEN_HAS_NO_LINKED_ACCOUNT = "Confirmation Token has no account to confirm";
    private static final String ERR_USER_HAS_NO_LOCAL_ACCOUNT = "User has no local account";

    private final AccountDao accountDao;
    private final SymmetricCryptTool cryptTool;
    private final TokenService tokenService;
    private final UserService userService;
    private final EmailService emailService;

    public static final String ERR_EMAIL_ALREADY_EXISTS = "Email already used";
    public static final String ERR_ENCRYPTION_FAILED = "Failed to encrypt plain text value before saving";

    private String accountToSearch;

    public boolean isAccountAlreadyExists(final String accountName, final AccountType accountType) {
        if (StringUtils.isBlank(accountName)) return false;
        this.accountToSearch = accountName;

        List<Account> accounts = accountDao.findByAccountType(accountType);
        Account sameAccount = accounts.parallelStream()
                .filter(this::accountHasGivenAccountName)
                .findFirst()
                .orElse(null);
        return Objects.nonNull(sameAccount);
    }

    public boolean isAccountUnique(final String accountName, final AccountType accountType) {
        return !isAccountAlreadyExists(accountName, accountType);
    }

    public OperationResult createLocalAccount(final User user) {
        Account localAccount = Account.create(AccountType.LOCAL).forUser(user);
        localAccount.setAccountName(user.getUsername());
        localAccount.setConfirmed(false);

        try {
            accountDao.save(localAccount);
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail();
        }
    }

    public OperationResult createEmailAccount(final User user, final String email) {
        OperationResult emailValidationResult = emailService.isEmailValid(email);
        if (emailValidationResult.notOk()) {
            return emailValidationResult;
        }

        if (isAccountUnique(email, AccountType.EMAIL)) {
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
            return OperationResult.success().addPayload(emailAccount);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail();
        }
    }

    public OperationResult confirmAccount(String tokenString) {
        try {
            //search token - Op.notFound()
            Optional<Token> token = tokenService.getToken(tokenString);
            if (token.isEmpty()) {
                log.info("{} token {} not found", TAG, tokenString);
                return OperationResult.elementNotFound().withMessage(ERR_NO_SUCH_TOKEN_FOUND);
            }

            //search account linked to confirmation token
            Account accountToConfirm = token.get().getConfirmationFor();
            if (accountToConfirm == null) {
                return OperationResult.generalFail().withMessage(ERR_TOKEN_HAS_NO_LINKED_ACCOUNT);
            }

            // confirming account
            accountToConfirm.setConfirmed(true);
            accountDao.save(accountToConfirm);

            User user = token.get().getUser();
            Optional<Account> userLocalAccount =
                    accountDao.findByUserAndAccountType(user, AccountType.LOCAL);
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

    public Optional<Account> getAccount(User user, AccountType accountType) {
        return accountDao.findByUserAndAccountType(user, accountType);
    }

    public Optional<String> decryptAccountName(Account account) {
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

    private boolean accountHasGivenAccountName(Account account) {
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
