package pm.axe.services.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pm.axe.db.models.*;
import pm.axe.internal.RegisterUserInput;
import pm.axe.result.OperationResult;
import pm.axe.senders.Senders;
import pm.axe.services.LinkInfoService;
import pm.axe.services.LinkService;
import pm.axe.services.user.rollback.RollbackService;
import pm.axe.services.user.rollback.RollbackTask;
import pm.axe.users.AccountType;
import pm.axe.users.TokenType;

import java.util.*;

/**
 * Service, that performs user-related operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserOperationsService {
    private static final String TAG = "[" + UserOperationsService.class.getSimpleName() + "]";
    private static final String USER_DELETION_OP = "(Delete User)";
    public static final String TELEGRAM_TOKEN_KEY = "telegramToken";
    private final Stack<RollbackTask> rollbackTasks = new Stack<>();
    private final RollbackService rollbackService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final AccountService accountService;
    private final TokenService tokenService;
    private final LinkInfoService linkInfoService;
    private final LinkService linkService;
    private final Senders senders;

    /**
     * Registers new User in System.
     * Creates new records in App's Database and
     * requests {@link TokenType#ACCOUNT_CONFIRMATION_TOKEN} from {@link TokenService}.
     *
     * @param input {@link RegisterUserInput} with filled in fields.
     * @return {@link OperationResult#success()} or {@link OperationResult} with error status and message inside.
     */
    public OperationResult registerUser(final RegisterUserInput input) {
        //Create User Record
        OperationResult userCreateResult = userService.createUser(input.getUsername(), input.getPassword());
        if (userCreateResult.notOk()) {
            return userCreateResult;
        }
        User createdUser = userCreateResult.getPayload(User.class);
        rollbackTasks.push(RollbackTask.create(User.class, createdUser));

        //Create UserSettings Record
        OperationResult userSettingsCreateResult = userSettingsService.createNewSettings(createdUser);
        if (userSettingsCreateResult.notOk()) {
            rollbackService.rollback(rollbackTasks);
            return userSettingsCreateResult;
        }
        UserSettings userSettings = userSettingsCreateResult.getPayload(UserSettings.class);
        rollbackTasks.push(RollbackTask.create(UserSettings.class, userSettings));

        Account userAccount;
        if (StringUtils.isBlank(input.getEmail())) {
            //Create Local Account
            OperationResult createLocalAccountResult = accountService.createLocalAccount(createdUser);
            if (createLocalAccountResult.notOk()) {
                log.error("{} Failed to create local {}. OpResult: {}",
                        TAG, Account.class.getSimpleName(), createLocalAccountResult);
                rollbackService.rollback(rollbackTasks);
                return createLocalAccountResult;
            }
            Account localAccount = createLocalAccountResult.getPayload(Account.class);
            userAccount = localAccount;
            rollbackTasks.push(RollbackTask.create(Account.class, localAccount));
        } else {
            //Create Email Account
            OperationResult createEmailAccountResult = accountService.createEmailAccount(createdUser, input.getEmail());
            if (createEmailAccountResult.notOk()) {
                log.error("{} Failed to create email {}. OpResult: {}",
                        TAG, Account.class.getSimpleName(), createEmailAccountResult);
                rollbackService.rollback(rollbackTasks);
                return createEmailAccountResult;
            }
            Account emailAccount = createEmailAccountResult.getPayload(Account.class);
            userAccount = emailAccount;
            rollbackTasks.push(RollbackTask.create(Account.class, emailAccount));
        }
        //UserSettings change Main Channel to Email
        userSettings.setMainChannel(userAccount.getType());

        //if TFA (2-factor Auth) enabled
        if (input.isTfaEnabled() && userAccount.getType() == AccountType.EMAIL) {
            //Set TFA enabled and update its channel to Email
            userSettings.setTfaEnabled(true);
            userSettings.setTfaChannel(AccountType.EMAIL);
        }
        //Save UserSettings
        OperationResult saveChannelUpdatesResult = userSettingsService.updateUserSettings(userSettings);
        if (saveChannelUpdatesResult.notOk()) {
            log.error("{} Failed to update {}. OpResult: {}",
                    TAG, UserSettings.class.getSimpleName(), saveChannelUpdatesResult);
        }
        //Create and send - confirmation email for Accounts with Email set.
        if (userAccount.getType() == AccountType.EMAIL) {
            //Create Confirmation Token
            OperationResult createConfirmationTokenResult =
                    tokenService.createConfirmationToken(createdUser, userAccount);
            if (createConfirmationTokenResult.notOk()) {
                log.error("{} failed to create confirmation token for {}. OpResult: {}",
                        TAG, createdUser.getUsername(), createConfirmationTokenResult);
                return createConfirmationTokenResult;
            }
            Token confirmationToken = createConfirmationTokenResult.getPayload(Token.class);
            rollbackTasks.push(RollbackTask.create(Token.class, confirmationToken));
            //Send it
            log.info("{} Successfully created {}({}) for user '{}'",
                    TAG, confirmationToken.getTokenType(), confirmationToken.getToken(), createdUser.getUsername());
            OperationResult sendResult = senders.getSender(AccountType.EMAIL).send(confirmationToken, input.getEmail());
            if (sendResult.notOk()) {
                log.warn("{} Unable to send created {} to {}. OpResult: {}",
                        TAG, confirmationToken.getTokenType(), input.getEmail(), sendResult);
                log.warn("{} Requesting Rollback", TAG);
                rollbackService.rollback(rollbackTasks);
            }
        }
        //Create Telegram Confirmation Token
        Token telegramConfirmationToken;
        OperationResult createTelegramTokenResult = tokenService.createTelegramConfirmationToken(createdUser);
        if (createTelegramTokenResult.ok()) {
            telegramConfirmationToken = createTelegramTokenResult.getPayload(Token.class);
            rollbackTasks.push(RollbackTask.create(Token.class, telegramConfirmationToken));
        } else {
            telegramConfirmationToken = null;
            log.warn("{} failed to create Telegram token for {}. OpResult: {}",
                    TAG, createdUser.getUsername(), createTelegramTokenResult);
        }

        //Report success back
        OperationResult success = OperationResult.success();
        return Objects.isNull(telegramConfirmationToken)
                ? success : success.addPayload(TELEGRAM_TOKEN_KEY, telegramConfirmationToken.getToken());
    }

    /**
     * Deletes User and its Accounts.
     *
     * @param user {@link User} record to delete
     * @param force delete {@link User}, event if it has connected records in DB.
     *              This will delete those records as well.
     * @return {@link OperationResult#ok()}
     */
    public OperationResult deleteUser(final User user, final boolean force) {
        OperationResult opResult;
        if (user.isConfirmed() && !force) {
            log.warn("{} {} User '{}' is confirmed (has at least one confirmed account). "
                            + "Cannot delete User without FORCE",
                    TAG, USER_DELETION_OP, user.getUsername());
            return OperationResult.banned().withMessage("Cannot delete confirmed user. Force required.");
        }
        boolean userHasLinks = linkInfoService.isUserHasLinks(user);
        if (userHasLinks && force) {
            log.info("{} {} OK. User '{}' has links. Deleting them with force!",
                    TAG, USER_DELETION_OP, user.getUsername());
            //get all linkInfo
            List<LinkInfo> allUsersLinksInfoRecords = linkInfoService.getAllRecordsOwnedByUser(user);
            List<Link> allUserLinks = new ArrayList<>();
           for (LinkInfo linkInfo : allUsersLinksInfoRecords) {
               Optional<Link> linkOptional = linkService.getLinkByLinkInfo(linkInfo);
               if (linkOptional.isPresent()) {
                   allUserLinks.add(linkOptional.get());
               } else {
                   log.warn("{} {} Suddenly no {} record corresponding with {}. Skipping deleting {} record",
                           TAG, USER_DELETION_OP, Link.class.getSimpleName(),
                           LinkInfo.class.getSimpleName(), Link.class.getSimpleName());
               }
           }
           //delete all LinkInfos first
            for (LinkInfo li : allUsersLinksInfoRecords) {
                linkInfoService.deleteLinkInfo(li.getIdent());
            }
            log.info("{} {} All {} records for '{}' were deleted",
                    TAG, USER_DELETION_OP, LinkInfo.class.getSimpleName(), user.getUsername());
            //delete all links
            for (Link l : allUserLinks) {
                opResult = linkService.deleteLinkWithIdent(l.getIdent());
                if (opResult.notOk()) {
                    log.info("{} {} failed to delete {} for '{}'. OpResult: {}",
                            TAG, USER_DELETION_OP, Link.class.getSimpleName(), user.getUsername(), opResult);
                    return opResult;
                }
            }
            log.info("{} {} All {} records for '{}' were deleted",
                    TAG, USER_DELETION_OP, Link.class.getSimpleName(), user.getUsername());
        } else if (userHasLinks) {
            //cannot proceed w/o force
            log.warn("{} {} User '{}' has links. Cannot delete User without FORCE",
                    TAG, USER_DELETION_OP, user.getUsername());
            return OperationResult.banned()
                    .withMessage(String.format("User '%s' has links. Force required.",
                            user.getUsername()));
        } else {
            //no links
            log.info("{} {} User '{}' has no links. Great! Next step.", TAG, USER_DELETION_OP, user.getUsername());
        }

        //remove user's tokens
        List<Token> usersTokens = tokenService.getAllTokensOwnedByUser(user);
        for (Token t : usersTokens) {
            opResult = tokenService.deleteToken(t.getToken());
            if (opResult.notOk()) {
                log.info("{} {} failed to delete {} for '{}'. OpResult: {}",
                        TAG, USER_DELETION_OP, Token.class.getSimpleName(), user.getUsername(), opResult);
                return opResult;
            }
        }
        log.info("{} {} All {}s for '{}' were deleted",
                TAG, USER_DELETION_OP, Token.class.getSimpleName(), user.getUsername());
        //remove user settings
        Optional<UserSettings> userSettings = userSettingsService.getUserSettings(user);
        userSettings.ifPresent(userSettingsService::deleteUserSettings);
        log.info("{} {} '{}' {} were deleted",
                TAG, USER_DELETION_OP, user.getUsername(), UserSettings.class.getSimpleName());
        //remove accounts
        List<Account> userAccounts = accountService.getAllAccountsLinkedWithUser(user);
        for (Account a : userAccounts) {
            opResult = accountService.deleteAccount(a);
            if (opResult.notOk()) {
                log.info("{} {} failed to delete {} for '{}'. OpResult: {}",
                        TAG, USER_DELETION_OP, Account.class.getSimpleName(), user.getUsername(), opResult);
                return opResult;
            }
        }
        log.info("{} {} All {}s linked with '{}' were deleted",
                TAG, USER_DELETION_OP, Account.class.getSimpleName(), user.getUsername());
        //remove user record
        OperationResult deletionResult = userService.deleteUser(user);
        log.info("{} {} {} '{}' is deleted. Done!",
                TAG, USER_DELETION_OP, User.class.getSimpleName(), user.getUsername());
        return deletionResult;
    }
}
