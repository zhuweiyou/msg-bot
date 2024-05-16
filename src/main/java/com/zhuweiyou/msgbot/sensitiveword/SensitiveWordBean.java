package com.zhuweiyou.msgbot.sensitiveword;

import com.github.houbb.sensitive.word.api.IWordAllow;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.api.IWordReplace;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveWordBean {
	private final IWordAllow wordAllow;
	private final IWordDeny wordDeny;
	private final IWordReplace wordReplace;

	@Autowired
	public SensitiveWordBean(IWordAllow wordAllow, IWordDeny wordDeny, IWordReplace wordReplace) {
		this.wordAllow = wordAllow;
		this.wordDeny = wordDeny;
		this.wordReplace = wordReplace;
	}

	@Bean
	public SensitiveWordBs sensitiveWordBs() {
		return SensitiveWordBs.newInstance()
			.charIgnore(SensitiveWordCharIgnores.specialChars())
			.wordReplace(wordReplace)
			.wordDeny(wordDeny)
			.wordAllow(wordAllow)
			.init();
	}
}
