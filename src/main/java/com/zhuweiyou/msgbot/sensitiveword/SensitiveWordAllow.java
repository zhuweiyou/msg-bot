package com.zhuweiyou.msgbot.sensitiveword;

import com.github.houbb.sensitive.word.api.IWordAllow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SensitiveWordAllow implements IWordAllow {
	private final SensitiveWordConfig sensitiveWordConfig;

	@Autowired
	public SensitiveWordAllow(SensitiveWordConfig sensitiveWordConfig) {
		this.sensitiveWordConfig = sensitiveWordConfig;
	}

	@Override
	public List<String> allow() {
		return sensitiveWordConfig.getAllowList();
	}
}
