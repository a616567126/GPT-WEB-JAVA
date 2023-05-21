package com.intelligent.bot.model.req.mj;

import lombok.Data;

@Data
public class UVSubmitReq {
	/**
	 * 自定义参数, task中保留.
	 */
	private String state;
	/**
	 * content: id u1.
	 */
	private String content;
}
