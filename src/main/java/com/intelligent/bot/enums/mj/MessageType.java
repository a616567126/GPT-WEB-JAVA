package com.intelligent.bot.enums.mj;


public enum MessageType {
	/**
	 * 创建.
	 */
	CREATE,
	/**
	 * 修改.
	 */
	UPDATE,
	/**
	 * 删除.
	 */
	DELETE;

	public static MessageType of(String type) {
		if(type.equals("MESSAGE_CREATE")){
			return CREATE;
		}
		if(type.equals("MESSAGE_UPDATE")){
			return UPDATE;
		}
		if(type.equals("MESSAGE_DELETE")){
			return DELETE;
		}
		return null;
	}
}
