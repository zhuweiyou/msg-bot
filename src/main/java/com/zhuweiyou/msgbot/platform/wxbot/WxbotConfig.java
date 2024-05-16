package com.zhuweiyou.msgbot.platform.wxbot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "platform.wxbot")
@Data
public class WxbotConfig {
	private String botWxid;
	private String adminWxid;
	private String apiUrl;
}
