package com.intelligent.bot.model.req.spark;


import lombok.Data;


@Data
public class ChatReq {

	/**
	 * 内容
	 */
	private String problem;

	/**
	 * 日志id
	 */
	private Long logId;


	/**
	 * 模型类型2/3
	 */
	private Integer type = 2;
}
