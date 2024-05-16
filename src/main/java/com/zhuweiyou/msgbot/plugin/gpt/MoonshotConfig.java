package com.zhuweiyou.msgbot.plugin.gpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "plugin.gpt.moonshot")
@Data
public class MoonshotConfig {
	private List<String> apiKeyList;

	private int apiKeyIndex;

	public String getNextApiKey() {
		String apiKey = apiKeyList.get(apiKeyIndex);
		apiKeyIndex = (apiKeyIndex + 1) % apiKeyList.size();
		return apiKey;
	}
}
