package com.intelligent.bot.model.mj.doman;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class DomainObject implements Serializable {

	@Getter
	@Setter
	protected String id;

	@JsonIgnore
	private final transient Object lock = new Object();

	public void sleep() throws InterruptedException {
		synchronized (this.lock) {
			this.lock.wait();
		}
	}

	public void awake() {
		synchronized (this.lock) {
			this.lock.notifyAll();
		}
	}

	private String notifyHook;

	private String finalPrompt;

	private String messageId;

	private String messageHash;

	private String progressMessageId;

	private Integer flags;

	private String nonce;

	private Integer code;

	private String description;


}
