package com.zhuweiyou.msgbot.plugin.translator;

import lombok.Data;

@Data
public class TranslatorResult {
	private String name;
	private String result;

	@Override
	public String toString() {
		return String.format("【%s】\n%s", name, result);
	}
}
