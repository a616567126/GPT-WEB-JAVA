package com.intelligent.bot.model.req.mj;

import lombok.Data;

import java.util.List;

@Data
public class SubmitBlendReq  {

	/**
	 * 图片base64数组
	 */
	private List<String> base64Array;
}
