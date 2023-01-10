package pm.axe.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pm.axe.redis.dao.RobotsRedisDao;

import java.util.List;

/**
 * List of automated software, which makes single request per time.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Service
public final class RobotsService {
    private static RobotsService instance;

    private final RobotsRedisDao robotsRedisDao;

    /**
     * Makes {@link RobotsService} be accessible from static context aka POJO.
     *
     * @return {@link RobotsService} object stored by {@link #init()}.
     */
    public static RobotsService getInstance() {
        return instance;
    }

    @PostConstruct
    private void init() {
        RobotsService.instance = this;
    }

    /**
     * Check if provider client is robot.
     *
     * @param userAgent string with UserAgent aka Browser to check
     * @return true if client is robot (automatic software), false if not.
     */
    public boolean isRobot(final String userAgent) {
        if (StringUtils.isBlank(userAgent)) return true; //device with empty UA isn't valid client.
        List<String> robotList = robotsRedisDao.getRobots();
        boolean isRobot = false;
        for (String robot : robotList) {
            if (userAgent.contains(robot)) {
                isRobot = true;
                break;
            }
        }
        return isRobot;
    }
}
