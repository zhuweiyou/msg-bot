package com.zhuweiyou.msgbot.plugin.translator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "plugin.translator.baidu")
@Data
public class BaiduConfig {
	// 百度翻译 收费版key
	private String appId;
	private String appKey;
}
