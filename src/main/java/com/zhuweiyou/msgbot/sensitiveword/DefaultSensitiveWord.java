package com.zhuweiyou.msgbot.sensitiveword;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSensitiveWord implements SensitiveWord {
	private final SensitiveWordBs sensitiveWordBs;

	@Autowired
	public DefaultSensitiveWord(SensitiveWordBs sensitiveWordBs) {
		this.sensitiveWordBs = sensitiveWordBs;
	}

	@Override
	public String replace(String word) {
		if (Strings.isBlank(word)) {
			return "";
		}
		return sensitiveWordBs.replace(word);
	}
}
