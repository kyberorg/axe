package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.yals.constants.App;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * App-wide tools
 *
 * @since 1.0
 */
@Component
public class AppUtils {

    private Environment env;

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final String DUMMY_HOST = "DummyHost";
    private static final String DUMMY_TOKEN = "dummyToken";

    public AppUtils(Environment env) {
        this.env = env;
    }

    public String getAPIHostPort() {
        return "localhost" + ":" + env.getProperty(App.Properties.SERVER_PORT, "8080");
    }

    public String getServerUrl() {
        String serverUrl = env.getProperty(App.Properties.SERVER_URL);
        return StringUtils.isNotBlank(serverUrl) ? serverUrl : DUMMY_HOST;
    }

    public String getTelegramToken() {
        String token = env.getProperty(App.Properties.TELEGRAM_TOKEN);
        return StringUtils.isNotBlank(token) ? token : DUMMY_TOKEN;
    }
}
