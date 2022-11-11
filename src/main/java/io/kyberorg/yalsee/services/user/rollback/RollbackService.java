package io.kyberorg.yalsee.services.user.rollback;

import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.dao.TokenDao;
import io.kyberorg.yalsee.dao.UserDao;
import io.kyberorg.yalsee.exception.error.YalseeErrorBuilder;
import io.kyberorg.yalsee.models.BaseModel;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Locale;
import java.util.Stack;

/**
 * Service, that performs rollbacks.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RollbackService {
    private final ErrorUtils errorUtils;
    private final UserDao userDao;
    private final TokenDao tokenDao;

    public static final String TAG = "[" + RollbackService.class.getSimpleName() + "]";
    public static final String ERR_NO_SUCH_DAO = "No corresponding DAO found for given model";

    @Async
    public void rollback(final Stack<RollbackTask> rollbackTasks) {
        while (!rollbackTasks.isEmpty()) {
            RollbackTask task = rollbackTasks.pop();
            OperationResult result = noOp(task);
            if (result.notOk()) {
                StringBuilder message = new StringBuilder("Exception on User Rollback. ");
                message.append("Current task: ").append(task.getName());
                message.append("Error: ").append(result.getMessage());
                message.append("Remaining (un-done) tasks are: ");
                for (RollbackTask t : rollbackTasks) {
                    message.append(t.getName());
                }
                reportToBugsnag(message.toString(), HttpCode.SERVER_ERROR);
                return;
            }
        }
    }

    private OperationResult performRollback(final RollbackTask task) {
        CrudRepository<? extends BaseModel, Long> dao = getDaoByModel(task.getModel());
        if (dao == null) return OperationResult.elementNotFound().withMessage(ERR_NO_SUCH_DAO);
        try {
            dao.deleteById(task.getRecordId());
            log.debug("{} {} executed successfully", TAG, task.getName());
            return OperationResult.success();
        } catch (CannotCreateTransactionException e) {
            reportToBugsnag("Database is DOWN", HttpCode.APP_IS_DOWN);
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on rolling changes back.", TAG);
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    private OperationResult noOp(final RollbackTask task) {
        log.info("{} Removing record with ID {} from {} table",
                TAG, task.getRecordId(), task.getModel().getSimpleName());
        return OperationResult.generalFail().withMessage("Failed with test purpose");
    }

    private CrudRepository<? extends BaseModel, Long> getDaoByModel(final Class<? extends BaseModel> model) {
        return switch (model.getSimpleName().toLowerCase(Locale.ROOT)) {
            case "user" -> userDao;
            case "token" -> tokenDao;
            default -> null;
        };
    }

    private void reportToBugsnag(final String techMessage, final int code) {
        errorUtils.reportToBugsnag(YalseeErrorBuilder
                .withTechMessage(techMessage)
                .withStatus(code)
                .build());
    }
}
