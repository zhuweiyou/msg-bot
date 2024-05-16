package com.zhuweiyou.msgbot.sensitiveword;

import com.github.houbb.pinyin.util.PinyinHelper;
import com.github.houbb.sensitive.word.api.IWordContext;
import com.github.houbb.sensitive.word.api.IWordReplace;
import com.github.houbb.sensitive.word.api.IWordResult;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import org.springframework.stereotype.Component;

@Component
public class SensitiveWordReplace implements IWordReplace {
	@Override
	public void replace(StringBuilder stringBuilder, char[] chars, IWordResult wordResult, IWordContext wordContext) {
		String sensitiveWord = InnerWordCharUtils.getString(chars, wordResult);
		stringBuilder.append(PinyinHelper.toPinyin(sensitiveWord));
	}
}
