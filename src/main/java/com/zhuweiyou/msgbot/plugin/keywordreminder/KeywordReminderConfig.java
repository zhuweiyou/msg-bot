package com.zhuweiyou.msgbot.plugin.keywordreminder;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@ConfigurationProperties("plugin.keywordreminder")
@Data
@Component
public class KeywordReminderConfig {
	private String keywordRegex;

	@Cacheable(value = "KeywordReminderConfig.getKeywordPattern")
	public Pattern getKeywordPattern() {
		return Pattern.compile(keywordRegex, Pattern.CASE_INSENSITIVE);
	}
}
