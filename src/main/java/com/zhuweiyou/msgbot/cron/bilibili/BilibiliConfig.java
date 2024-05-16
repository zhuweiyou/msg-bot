package com.zhuweiyou.msgbot.cron.bilibili;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cron.bilibili")
@Data
public class BilibiliConfig {
	private List<String> chuanggeWxidList;
}
