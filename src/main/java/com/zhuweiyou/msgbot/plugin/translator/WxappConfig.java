package com.zhuweiyou.msgbot.plugin.translator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "plugin.translator.wxapp")
@Data
public class WxappConfig {
	// 腾讯翻译君 小程序抓包获得此参数
	private String guid;
}
