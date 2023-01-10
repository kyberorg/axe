package pm.axe.telegram;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pm.axe.db.models.Account;
import pm.axe.db.models.User;
import pm.axe.events.user.UserDeletedEvent;
import pm.axe.services.user.AccountService;
import pm.axe.users.AccountType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Hot Map with TelegramUser - AxeUser Mapping. Prevent searching from Database.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class TelegramUserMapping {
    private static final String TAG = "[" + TelegramUserMapping.class.getSimpleName() + "]";
    private final Map<String, User> mapping = new HashMap<>();

    private final AccountService accountService;

    @PostConstruct
    private void subscribe() {
        EventBus.getDefault().register(this);
    }

    /**
     * Creates new {@link TelegramUserMapping} record.
     *
     * @param tgUser non-empty string with telegram user.
     * @param axeUser corresponding Axe {@link User}.
     */
    public void createMapping(final String tgUser, final User axeUser) {
        if (StringUtils.isBlank(tgUser)) {
            throw new IllegalArgumentException("Cannot create mapping for empty tgUser");
        }
        if (axeUser == null || axeUser.equals(User.createPseudoUser())) {
            throw new IllegalArgumentException("Cannot create mapping for empty or pseudo user");
        }
        mapping.put(tgUser, axeUser);
        log.info("{} Mapping created. User: {}", TAG, axeUser.getUsername());
    }

    /**
     * Gets mapped {@link User}.
     *
     * @param tgUser non-empty string with telegram username.
     * @return {@link Optional} with corresponding Axe {@link User} if found,
     * {@link Optional#empty()} - if no match found.
     */
    public Optional<User> getAxeUser(final String tgUser) {
        return Optional.ofNullable(mapping.get(tgUser));
    }

    /**
     * Defines if telegram user has {@link TelegramUserMapping} or not.
     *
     * @param tgUser non-empty string with telegram username.
     * @return true - if telegram user mapped with Axe {@link User}, false if not.
     */
    public boolean hasMapping(final String tgUser) {
        return mapping.containsKey(tgUser);
    }

    /**
     * Deletes record from {@link TelegramUserMapping}.
     *
     * @param tgUser non-empty string with telegram username.
     */
    public void deleteMapping(final String tgUser) {
        if (StringUtils.isBlank(tgUser)) {
            throw new IllegalArgumentException("Telegram USer cannot be blank");
        }
        if (mapping.containsKey(tgUser)) {
            mapping.remove(tgUser);
            log.info("{} Mapping deleted. tgUser: {}", TAG, tgUser);
        } else {
            log.warn("{} failed to delete mapping. No mapping found for tgUser {}", TAG, tgUser);
        }
    }

    /**
     * Deletes record from {@link TelegramUserMapping} for given Axe {@link User}.
     *
     * @param user mapped Axe {@link User}
     */
    public void deleteMappingForAxeUser(final User user) {
        String foundRecordKey = "";
        for (Map.Entry<String, User> record: mapping.entrySet()) {
            if (record.getValue().equals(user)) {
                foundRecordKey = record.getKey();
                break;
            }
        }
        if (StringUtils.isNotBlank(foundRecordKey)) {
            mapping.remove(foundRecordKey);
            log.info("{} Telegram Mapping deleted for {}", TAG, user.getUsername());
        } else {
            log.warn("{} failed to delete mapping. No mapping found for tgUser {}", TAG, user.getUsername());
        }
    }

    /**
     * {@link UserDeletedEvent} handler. It deletes active mapping for deleted Axe {@link User}, if any exists.
     *
     * @param event {@link UserDeletedEvent}, that contains deleted {@link User} record.
     */
    @Subscribe
    public void onUserDeleted(final UserDeletedEvent event) {
        if (event.getDeletedUser() == null || event.getDeletedUser().equals(User.createPseudoUser())) {
            log.warn("{} got {} with empty or pseudo user. Ignoring", TAG, UserDeletedEvent.class.getSimpleName());
            return;
        }
        deleteMappingForAxeUser(event.getDeletedUser());
    }

    /**
     * Populates {@link TelegramUserMapping} at {@link pm.axe.Application} start-time.
     * This method gets Telegram {@link Account}s from Database.
     */
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void prePopulateMapping() {
        log.info("{} PrePopulation started", TAG);
        List<Account> telegramAccounts = accountService.getAllAccountsByType(AccountType.TELEGRAM);
        List<ImmutablePair<Optional<String>, User>> tgAccounts = telegramAccounts.parallelStream()
                .map(account -> {
                    Optional<String> tgUser = accountService.decryptAccountName(account);
                    User axeUser = account.getUser();
                    return ImmutablePair.of(tgUser, axeUser);
                })
                .toList();
        tgAccounts.forEach(tgAcc -> {
            if (tgAcc.getLeft().isPresent()) {
                createMapping(tgAcc.getLeft().get(), tgAcc.getRight());
            }
        });
        log.info("{} PrePopulation done. {} mapping(s) were pre-populated.", TAG, mapping.size());
    }

    @PreDestroy
    private void unsubscribe() {
        EventBus.getDefault().unregister(this);
    }
}
