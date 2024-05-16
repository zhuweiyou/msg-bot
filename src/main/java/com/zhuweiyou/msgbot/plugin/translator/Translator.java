package com.zhuweiyou.msgbot.plugin.translator;

public interface Translator {
	String getName();

	String translate(String input) throws Exception;
}
