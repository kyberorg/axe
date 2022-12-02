package pm.axe.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pm.axe.db.models.TimeModel;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkInfoService;
import pm.axe.services.user.UserOperationsService;
import pm.axe.services.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class GarbageUserCollector {
    private static final String TAG = "[" + GarbageUserCollector.class.getSimpleName() + "]";
    private static final String OP = "(Users GC)";
    private final UserOperationsService userOpsService;
    private final UserService userService;
    private final LinkInfoService linkInfoService;

    @Value("${app.users.garbage-after-seconds}")
    private int garbageAfterSeconds;

    @Getter
    private final Semaphore mutex = new Semaphore(1);

    /**
     * Collects and deletes Garbage (aka inactive) users. See {@link #isGarbageUser(User)} for Garbage User Criteria.
     *
     * @throws InterruptedException when attempt to run method twice.
     */
    @Async
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void collectAndDelete() throws InterruptedException {
        log.info("{} {} Started", TAG, OP);
        mutex.acquire();
        List<User> allUsers = userService.getAllUsers();
        List<User> garbageUsers = allUsers.stream()
                .filter(this::notDefaultUser)
                .filter(this::isGarbageUser)
                .toList();
        if (garbageUsers.isEmpty()) {
            log.info("{} {} no Garbage Users were found.", TAG, OP);
        } else {
            log.info("{} {} There are {} Garbage Users found", TAG, OP, garbageUsers.size());
            int deletedUsers = 0;
            for (User user: garbageUsers) {
                OperationResult opResult = userOpsService.deleteUser(user, false);
                if (opResult.notOk()) {
                    log.warn("{} {} Failed to delete Garbage User {}. OpResult: {}",
                            TAG, OP, user, opResult);
                } else {
                    deletedUsers++;
                }
            }
            log.info("{} {} Success. {} Garbage Users were deleted", TAG, OP, deletedUsers);
        }
        log.info("{} {} Completed", TAG, OP);
        mutex.release();
    }

    private boolean isGarbageUser(final User user) {
        //not confirmed
        if (user.isConfirmed()) return false;
        //has no links
        if (linkInfoService.isUserHasLinks(user)) return false;
        //created long time ago
        Instant garbageAfter = user.getCreated().toInstant().plusSeconds(garbageAfterSeconds);
        Instant now = TimeModel.now().toInstant();
        return now.isAfter(garbageAfter);
    }

    private boolean notDefaultUser(final User user) {
        return !Objects.equals(user.getId(), userService.getDefaultUser().getId());
    }
}
