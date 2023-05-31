package com.intelligent.bot.service.mj.impl;

import com.intelligent.bot.api.midjourney.support.Task;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.utils.sys.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTaskStoreServiceImpl implements TaskStoreService {

	@Override
	public void saveTask(Task task) {
		RedisUtil.setCacheObject(getRedisKey(task.getId()), task,7L, TimeUnit.DAYS);
	}

	@Override
	public void deleteTask(String id) {
		RedisUtil.deleteObject(getRedisKey(id));
	}

	@Override
	public Task getTask(String id) {
		return RedisUtil.getCacheObject(getRedisKey(id));
	}

	@Override
	public List<Task> listTask() {
		List<Task> tasks = new ArrayList<Task>();
		Set<String> keys = RedisUtil.getKeys(CommonConst.KEY_PREFIX + "*");
		if(null != keys){
			keys.forEach(k ->{
				tasks.add(getTask(k));
			});
		}
		return tasks;
	}

	private String getRedisKey(String id) {
		return CommonConst.KEY_PREFIX + id;
	}

}
