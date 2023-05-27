package com.intelligent.bot.utils.sys;

import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class QueueUtil {

    private static final String QUEUE_KEY = "user_queue";

    private final RedisTemplate<String, String> redisTemplate;

    public QueueUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addUserToQueue(String value) {
        if(getCurrentQueueLength() > CommonConst.QUEUE_SIZE){
            throw new E("队列已满请等待");
        }
        redisTemplate.opsForList().rightPush(QUEUE_KEY, value);
    }

    public long getCurrentQueueLength() {
        return redisTemplate.opsForList().size(QUEUE_KEY);
    }

    public List<String> getCurrentQueue() {
        return redisTemplate.opsForList().range(QUEUE_KEY, 0, -1);
    }
    public int getPosition(String value) {
        List<String> list = getCurrentQueue();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                return i;
            }
        }
        return -1;  // 如果没有找到元素，则返回 -1 表示未找到
    }

    public void remove(String value) {
        redisTemplate.opsForList().remove(QUEUE_KEY, 0, value);
    }

}
