package com.zhuweiyou.msgbot.plugin.keywordreminder;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties("plugin.keywordreminder")
@Data
@Component
public class KeywordReminderConfig {
	private List<String> keywordList;
}
