package com.zhuweiyou.msgbot.platform.ntchat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "platform.ntchat")
@Data
public class NtchatConfig {
	private String botWxid;
	private String adminWxid;
	private String guid;
	private String apiUrl;
}
