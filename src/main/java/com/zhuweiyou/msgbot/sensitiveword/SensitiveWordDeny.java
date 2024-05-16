package com.zhuweiyou.msgbot.sensitiveword;

import com.github.houbb.sensitive.word.api.IWordDeny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SensitiveWordDeny implements IWordDeny {
	private final SensitiveWordConfig sensitiveWordConfig;

	@Autowired
	public SensitiveWordDeny(SensitiveWordConfig sensitiveWordConfig) {
		this.sensitiveWordConfig = sensitiveWordConfig;
	}

	@Override
	public List<String> deny() {
		return sensitiveWordConfig.getDenyList();
	}
}
