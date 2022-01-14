package io.kyberorg.yalsee.session;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * List of automated software, which makes single request per time.
 *
 * @since 3.8
 */
public final class RobotsList {
    private static final List<String> ROBOTS = new ArrayList<>();

    static {
        ROBOTS.add("updown.io");
        ROBOTS.add("unirest-java");
        ROBOTS.add("Go-http-client");
        ROBOTS.add("curl");
        ROBOTS.add("Linux Gnu (cow)");
        ROBOTS.add("python-requests");
        ROBOTS.add("Roku/DVP");
        ROBOTS.add("masscan");
        ROBOTS.add("gdnplus.com");
        ROBOTS.add("Python-urllib");
        ROBOTS.add("NetcraftSurveyAgent");
        ROBOTS.add("CensysInspect");
        ROBOTS.add("YandexBot");
        ROBOTS.add("bingbot");
        ROBOTS.add("SemrushBot");
        ROBOTS.add("Googlebot");
        ROBOTS.add("LightspeedSystemsCrawler");
        ROBOTS.add("swcd");
        ROBOTS.add("AhrefsBot");
        ROBOTS.add("TelegramBot");
    }

    private RobotsList() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Check if provider client is robot.
     *
     * @param userAgent string with UserAgent aka Browser to check
     * @return true if client is robot (automatic software), false if not.
     */
    public static boolean isRobot(final String userAgent) {
        if (StringUtils.isBlank(userAgent)) return true; //device with empty UA isn't valid client.
        boolean isRobot = false;
        for (String robot : ROBOTS) {
            if (userAgent.contains(robot)) {
                isRobot = true;
                break;
            }
        }
        return isRobot;
    }
}
