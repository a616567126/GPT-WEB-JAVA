package com.intelligent.bot.api.mj.support;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class BannedPromptHelper {
	private static final String BANNED_WORDS_FILE_PATH = "/home/spring/config/banned-words.txt";
	private final List<String> bannedWords;

	public BannedPromptHelper() {
		List<String> lines;
		File file = new File(BANNED_WORDS_FILE_PATH);
		if (file.exists()) {
			lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
		} else {
			URL resource = BannedPromptHelper.class.getResource("/banned-words.txt");
			lines = FileUtil.readLines(resource, StandardCharsets.UTF_8);
		}
		this.bannedWords = lines.stream().filter(CharSequenceUtil::isNotBlank).collect(Collectors.toList());
	}

	public boolean isBanned(String promptEn) {
		String finalPromptEn = promptEn.toLowerCase(Locale.ENGLISH);
		return this.bannedWords.stream().anyMatch(bannedWord -> Pattern.compile("\\b" + bannedWord + "\\b").matcher(finalPromptEn).find());
	}

}
