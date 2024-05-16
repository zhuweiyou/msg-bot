package com.zhuweiyou.msgbot.cron.weibo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cron.weibo")
@Data
public class WeiboConfig {
	private List<String> ganggangWxidList;
}
