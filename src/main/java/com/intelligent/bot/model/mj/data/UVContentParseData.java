package com.intelligent.bot.model.mj.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UVContentParseData extends ContentParseData {
	protected Integer index;
}
