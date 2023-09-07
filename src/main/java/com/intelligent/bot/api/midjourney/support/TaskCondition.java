package com.intelligent.bot.api.midjourney.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.Task;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


@Data
@Accessors(chain = true)
public class TaskCondition implements Predicate<Task> {

	private Long id;

	private List<TaskStatus> statusSet;
	private List<TaskAction> actionSet;

	private String prompt;
	private String promptEn;
	private String description;

	private String finalPromptEn;
	private Long relatedTaskId;
	private String messageId;
	private String messageHash;
	private String progressMessageId;
	private String nonce;



	@Override
	public boolean test(Task task) {
		if (null != this.id && !Objects.equals(this.id, task.getId())) {
			return false;
		}
		if (this.statusSet != null && !this.statusSet.isEmpty() && !this.statusSet.contains(task.getStatus())) {
			return false;
		}
		if (this.actionSet != null && !this.actionSet.isEmpty() && !this.actionSet.contains(task.getAction())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.prompt) && !this.prompt.equals(task.getPrompt())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.promptEn) && !this.promptEn.equals(task.getPromptEn())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.description) && !CharSequenceUtil.contains(task.getDescription(), this.description)) {
			return false;
		}

		if (CharSequenceUtil.isNotBlank(this.finalPromptEn) && !this.finalPromptEn.equals(task.getFinalPrompt())) {
			return false;
		}
		if (null != this.relatedTaskId && !this.relatedTaskId.equals(task.getRelatedTaskId())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.messageId) && !this.messageId.equals(task.getMessageId())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.messageHash) && !this.messageHash.equals(task.getMessageHash())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.progressMessageId) && !this.progressMessageId.equals(task.getProgressMessageId())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.nonce) && !this.nonce.equals(task.getNonce())) {
			return false;
		}
		return true;
	}


}
