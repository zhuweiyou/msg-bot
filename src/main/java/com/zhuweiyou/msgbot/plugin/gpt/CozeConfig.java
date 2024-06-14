package com.zhuweiyou.msgbot.plugin.gpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
public class CozeConfig {
	@Bean
	@ConfigurationProperties(prefix = "plugin.gpt.coze.normal-bot")
	public CozeBot getNormalBot() {
		return new CozeBot();
	}

	@Bean
	@ConfigurationProperties(prefix = "plugin.gpt.coze.fuck-bot")
	public CozeBot getFuckBot() {
		return new CozeBot();
	}
}
