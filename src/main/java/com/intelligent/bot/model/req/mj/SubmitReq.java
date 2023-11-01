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

	/**
	 * 过滤关键字
	 */
	private String no;

	/**
	 * 版本
	 */
	private String version;

	/**
	 * 风格化
	 */
	private String stylize;

	/**
	 * 混乱
	 */
	private String chaos;

	/**
	 * 品质
	 */
	private String q;

	/**
	 * 尺寸
	 */
	private String ar;

	/**
	 * niji风格
	 */
	private String style;

	/**
	 * 出图模式 1.常规模式 --relax 2.极速模式--fast 3.涡轮模式--turbo
	 */
	private Integer plotMode = 1;
}
