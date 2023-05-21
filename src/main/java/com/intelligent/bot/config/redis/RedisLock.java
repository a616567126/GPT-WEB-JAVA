package com.intelligent.bot.config.redis;


import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class RedisLock {


    // todo  待优化，最好使用自定义的线程池，自定义工作队列和最大线程数。
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(4);
    @Resource
    RedissonClient redissonClient;

    /**
     * Redission获取锁
     *
     * @param lockKey      锁名
     * @param uuid         唯一标识
     * @param delaySeconds 过期时间
     * @param unit         单位
     * @return 是否获取成功
     */
    public boolean Rlock(String lockKey, final String uuid, long delaySeconds, final TimeUnit unit) {
        RLock rLock = redissonClient.getLock(lockKey);
        boolean success = false;
        try {
            // log.debug("===lock thread id is :{}", Thread.currentThread().getId());
            success = rLock.tryLock(0, delaySeconds, unit);
        } catch (InterruptedException e) {
            log.error("[RedisLock][Rlock]>>>> 加锁异常: ", e);
        }
        return success;
    }

    /**
     * Redission释放锁
     *
     * @param lockKey 锁名
     */
    public void Runlock(String lockKey) {
        RLock rLock = redissonClient.getLock(lockKey);
        log.debug("[RedisLock][Rlock]>>>> {}, status: {} === unlock thread id is: {}", rLock.isHeldByCurrentThread(), rLock.isLocked(),
                Thread.currentThread().getId());
        rLock.unlock();
    }

    /**
     * Redission延迟释放锁
     *
     * @param lockKey 锁名
     * @param delayTime 延迟时间
     * @param unit 单位
     */
    public void delayUnlock(final String lockKey, long delayTime, TimeUnit unit) {
        if (!StringUtils.hasText(lockKey)) {
            return;
        }
        if (delayTime <= 0) {
            Runlock(lockKey);
        } else {
            EXECUTOR_SERVICE.schedule(() -> Runlock(lockKey), delayTime, unit);
        }
    }
}
