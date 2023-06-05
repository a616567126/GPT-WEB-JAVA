package com.intelligent.bot.model.req.mj;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 提交Imagine或UV任务参数
 */
@Data
public class SubmitReq {
	/**
	 * prompt: action 为 IMAGINE 必传.
	 */
	@NotNull
	private String prompt;

	List<String> imgList;

}
