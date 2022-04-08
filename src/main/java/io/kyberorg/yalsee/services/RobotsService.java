package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.dao.RobotsRedisDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * List of automated software, which makes single request per time.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Service
public final class RobotsService {
    private static final List<String> ROBOTS_LIST = new ArrayList<>();

    static {
        ROBOTS_LIST.add("curl");
        ROBOTS_LIST.add("wget");
    }

    private static RobotsService instance;

    private final RobotsRedisDao robotsRedisDao;

    @Value("${redis.enabled}")
    private boolean isRedisEnabled;

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
        List<String> robotList;
        if (isRedisEnabled) {
            robotList = robotsRedisDao.getRobots();
        } else {
            robotList = ROBOTS_LIST;
        }
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
