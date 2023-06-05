package com.intelligent.bot.api.midjourney.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.MjTask;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


@Data
@Accessors(chain = true)
public class TaskCondition implements Predicate<MjTask> {

	private Long id;

	private String prompt;
	private String promptEn;
	private String finalPrompt;
	private String description;

	private Long relatedTaskId;
	private String messageId;

	private List<TaskStatus> statusSet;
	private List<TaskAction> actionSet;

	@Override
	public boolean test(MjTask task) {
		if (null != this.id && !Objects.equals(this.id, task.getId())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.prompt) && !this.prompt.equals(task.getPrompt())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.promptEn) && !this.promptEn.equals(task.getPromptEn())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.finalPrompt) && !this.finalPrompt.equals(task.getFinalPrompt())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.description) && !this.description.equals(task.getDescription())) {
			return false;
		}
		if (null != this.relatedTaskId && !this.relatedTaskId.equals(task.getRelatedTaskId())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.messageId) && !this.messageId.equals(task.getMessageId())) {
			return false;
		}

		if (this.statusSet != null && !this.statusSet.isEmpty() && !this.statusSet.contains(task.getStatus())) {
			return false;
		}
		if (this.actionSet != null && !this.actionSet.isEmpty() && !this.actionSet.contains(task.getTaskAction())) {
			return false;
		}
		return true;
	}


}
