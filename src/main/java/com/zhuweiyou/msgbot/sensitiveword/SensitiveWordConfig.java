package com.zhuweiyou.msgbot.sensitiveword;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sensitiveword")
@Data
public class SensitiveWordConfig {
	private List<String> denyList;
	private List<String> allowList;
}
