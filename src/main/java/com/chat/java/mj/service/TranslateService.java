package com.chat.java.mj.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TranslateService {

	String translateToEnglish(String prompt);

	boolean textToExamine(String prompt);



	default boolean containsChinese(String prompt) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(prompt);
		return m.find();
	}

}
