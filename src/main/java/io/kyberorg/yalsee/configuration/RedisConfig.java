package io.kyberorg.yalsee.configuration;

import io.kyberorg.yalsee.redis.pubsub.RedisMessageReceiver;
import io.kyberorg.yalsee.redis.pubsub.YalseeMessage;
import io.kyberorg.yalsee.redis.serializers.YalseeMessageGsonRedisSerializer;
import io.kyberorg.yalsee.redis.serializers.YalseeSessionGsonRedisSerializer;
import io.kyberorg.yalsee.session.YalseeSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis configuration.
 *
 * @since 3.8
 */
@Slf4j
@Configuration
public class RedisConfig {
    private static final String TAG = "[" + RedisConfig.class.getSimpleName() + "]";

    private static final String DEFAULT_REDIS_CHANNEL_NAME = "Yalsee";

    @Value("${redis.enabled}")
    private boolean isRedisEnabled;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.application.name}")
    private String appName;

    /**
     * Redis Server Connection configuration.
     *
     * @return {@link JedisConnectionFactory} with set params.
     */
    @Bean
    JedisConnectionFactory redisConnectionFactory() {
        if (!isRedisEnabled) return null;
        log.info("{} Connecting Redis at {}:{}. Database: {}", TAG, host, port, database);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        if (StringUtils.isNotBlank(password)) {
            log.debug("{} Authorizing at Redis with password {}", TAG, password);
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder =
                JedisClientConfiguration.builder();

        jedisClientConfigurationBuilder.connectTimeout(Duration.ofMillis(timeout));

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfigurationBuilder.build());
    }

    /**
     * Redis Template for working with {@link YalseeSession} objects.
     *
     * @return {@link RedisTemplate} configured to work with {@link YalseeSession}.
     */
    @Bean
    RedisTemplate<String, YalseeSession> yalseeSessionRedisTemplate() {
        if (!isRedisEnabled) return null;
        final YalseeSessionGsonRedisSerializer jsonSerializer = yalseeSessionGsonRedisSerializer();

        RedisTemplate<String, YalseeSession> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setDefaultSerializer(jsonSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(jsonSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean
    YalseeSessionGsonRedisSerializer yalseeSessionGsonRedisSerializer() {
        return new YalseeSessionGsonRedisSerializer();
    }

    /**
     * Redis Template for working with {@link YalseeMessage} objects.
     *
     * @return {@link RedisTemplate} configured to work with {@link YalseeMessage}.
     */
    @Bean
    RedisTemplate<String, YalseeMessage> messageRedisTemplate() {
        final YalseeMessageGsonRedisSerializer jsonSerializer = new YalseeMessageGsonRedisSerializer();

        RedisTemplate<String, YalseeMessage> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setDefaultSerializer(jsonSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(jsonSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * Bean that listens {@link #topic()} channel and receives messages from there.
     *
     * @return {@link RedisMessageReceiver} wrapped with {@link MessageListenerAdapter} to hide threads internals.
     */
    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageReceiver());
    }

    /**
     * Bean that represents channel. We use one channel per application.
     *
     * @return Redis's {@link ChannelTopic} named after application name from properties
     * or {@link #DEFAULT_REDIS_CHANNEL_NAME}.
     */
    @Bean
    ChannelTopic topic() {
        final String channelName = StringUtils.isNotBlank(appName) ? appName : DEFAULT_REDIS_CHANNEL_NAME;
        return new ChannelTopic(channelName);
    }

    /**
     * Bean that links {@link #messageListener()} with {@link #topic()}.
     *
     * @return {@link RedisMessageListenerContainer} that configured to connect Redis
     * and linked both {@link #messageListener()} and {@link #topic()}.
     */
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }
}
