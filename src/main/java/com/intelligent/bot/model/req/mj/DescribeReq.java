package com.intelligent.bot.model.req.mj;

import lombok.Data;

/**
 * 提交图生文任务参数
 */
@Data
public class DescribeReq {
	/**
	 * 自定义参数.
	 */
	private String state;
	/**
	 * 文件base64: data:image/png;base64,xxx.
	 */
	private String base64;

}
