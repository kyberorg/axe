package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.dao.base.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis DAO for Robots list.
 *
 * @since 3.8
 */
@RequiredArgsConstructor
@Repository
public class RobotsRedisDao extends RedisDao {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LIST_NAME = RedisDao.getApplicationPrefix() + "Robots";

    private ListOperations<String, String> listOps;

    @PostConstruct
    private void init() {
        listOps = redisTemplate.opsForList();
    }

    @Override
    protected long getRecordTtl() {
        return -1;
    }

    /**
     * Returns list of Robots (automatic software), what make single request per time.
     *
     * @return list of user agent patterns considered as robot software.
     */
    public List<String> getRobots() {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(LIST_NAME))) {
            return new ArrayList<>(0);
        } else {
            Long size = listOps.size(LIST_NAME);
            if (size == null || size.intValue() == 0) {
                return new ArrayList<>(0);
            } else {
                return listOps.range(LIST_NAME, 0, size);
            }
        }
    }
}
